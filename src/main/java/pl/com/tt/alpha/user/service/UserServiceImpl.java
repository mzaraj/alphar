package pl.com.tt.alpha.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.EntityEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
//import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.com.tt.alpha.audit.enums.AuditEnumsUser;
import pl.com.tt.alpha.audit.service.AuditEventService;
import pl.com.tt.alpha.common.exception.ObjectNotFoundException;
import pl.com.tt.alpha.configuration.Constants;
import pl.com.tt.alpha.filters.SpecificationBuilder;
import pl.com.tt.alpha.security.SecurityUtils;
//import pl.com.tt.alpha.security.ldap.LdapMapper;
import pl.com.tt.alpha.user.domain.AuthorityEntity;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.mapper.UserAuthoritiesMapper;
import pl.com.tt.alpha.user.mapper.UserMapper;
import pl.com.tt.alpha.user.repository.AuthorityRepository;
import pl.com.tt.alpha.user.repository.UserRepository;
import pl.com.tt.alpha.user.vm.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static pl.com.tt.alpha.common.converter.ImageConverter.encoder;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final AuthorityRepository authorityRepository;
	private final CacheManager cacheManager;
	private final UserMapper userMapper;
	private final UserAuthoritiesMapper userAuthoritiesMapper;


	@Autowired
	private final AuditEventService auditEventService;

	@Override
	public UserEntity createUser(UserVM userVM) {
		UserEntity user = userMapper.createNewUser(userVM);
		UserEntity saved = userRepository.save(user);
		this.clearUserCaches(saved);
		auditEventService.saveAudit(SecurityUtils.getCurrentUserOnlyLogin(), user.getId(), AuditEnumsUser.CREATE_USER);
		log.debug("Created Information for UserEntity: {}", saved);
		return saved;
	}

	@Override
	public UserVM addAvatarToUser(MultipartFile avatar) throws IOException {
		UserEntity user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserOnlyLogin()).orElseThrow(() -> new ObjectNotFoundException("Użytkownik z tym id nie istnieje"));
		if (nonNull(avatar)) {
			user.setAvatar(encoder(avatar));
		}
		return userMapper.toVm(userRepository.save(user));
	}

	/**
	 * Update basic information (first name, last name, email, language) for the current user.
	 *
	 * @param firstName first name of user
	 * @param lastName last name of user
	 * @param email email id of user
	 * @param langKey language key
	 */
	@Override
	public void updateUser(String firstName, String lastName, String email, String langKey) {
		SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(user -> {
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setEmail(email);
			user.setLangKey(langKey);
			this.clearUserCaches(user);
			log.debug("Changed Information for UserEntity: {}", user);
			auditEventService.saveAudit(SecurityUtils.getCurrentUserOnlyLogin(), user.getId(), AuditEnumsUser.UPDATE_USER);
		});
	}

	/**
	 * Update all information for a specific user, and return the modified user.
	 *
	 * @param userVM user to update
	 * @return updated user
	 */
	@Override
	public Optional<UserVM> updateUser(UserVM userVM) {
		return Optional.of(userRepository.findById(userVM.getId())).filter(Optional::isPresent).map(Optional::get).map(user -> {
			this.clearUserCaches(user);
			user.setLogin(userVM.getLogin());
			user.setFirstName(userVM.getFirstName());
			user.setLastName(userVM.getLastName());
			user.setEmail(userVM.getEmail());
			user.setActive(userVM.isActive());
			user.setLangKey(userVM.getLangKey());
			user.setCity(userVM.getCity());
			user.setBday(userVM.getBday());
			Set<AuthorityEntity> managedAuthorities = user.getAuthorities();
			managedAuthorities.clear();
			userVM.getAuthorities().stream().map(authorityRepository::findById).filter(Optional::isPresent).map(Optional::get).forEach(managedAuthorities::add);
			this.clearUserCaches(user);
			log.debug("Changed Information for UserEntity: {}", user);
			auditEventService.saveAudit(SecurityUtils.getCurrentUserOnlyLogin(), user.getId(), AuditEnumsUser.UPDATE_USER);
			return user;
		}).map(userMapper::toVm);
	}

	@Override
	public Optional<UserAuthoritiesVM> updateUserAuthorities(String login, UserAuthoritiesVM userAuthoritiesVM) {
		return Optional.of(userRepository.findOneByLogin(login)).filter(Optional::isPresent).map(Optional::get).map(user -> {
			this.clearUserCaches(user);
			Set<AuthorityEntity> managedAuthorities = user.getAuthorities();
			managedAuthorities.clear();
			userAuthoritiesVM.getAuthorities().stream().map(authorityRepository::findById).filter(Optional::isPresent).map(Optional::get)
							 .forEach(managedAuthorities::add);
			this.clearUserCaches(user);
			log.debug("Changed Authorities for UserEntity: {}", user);
			auditEventService.saveAudit(SecurityUtils.getCurrentUserOnlyLogin(), user.getId(), AuditEnumsUser.UPDATE_AUTHORITIES);
			return user;
		}).map(userAuthoritiesMapper::toVm);
	}

	@Override
	public void deleteUser(String login) {
		userRepository.findOneByLogin(login).ifPresent(user -> {
			userRepository.delete(user);
			this.clearUserCaches(user);
			auditEventService.saveAudit(SecurityUtils.getCurrentUserOnlyLogin(), user.getId(), AuditEnumsUser.DELETE_USER);
			log.debug("Deleted UserEntity: {}", user);
		});
	}

	@Override
	@Transactional(readOnly = true)
	public Page<UserVM> getAllManagedUsers(Pageable pageable) {
		return userRepository.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER).map(userMapper::toVm);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<UserEntity> getUserWithAuthoritiesByLogin(String login) {
		return userRepository.findOneWithAuthoritiesByLogin(login);
	}

    @Override
    public Long getUserId(String login) {
	    Long id = userRepository.findOneByLogin(login).get().getId();
	    if (id == null)
	        throw new ObjectNotFoundException("Użytkownik z tym loginem nie istnieje");
	    return id;
    }

    @Override
	@Transactional(readOnly = true)
	public Optional<UserEntity> getUserWithAuthorities(Long id) {
		return userRepository.findOneWithAuthoritiesById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<UserEntity> getUserWithAuthorities() {
		return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
	}

	/**
	 * @return a list of all the authorities
	 */
	@Override
	public List<String> getAuthorities() {
		return authorityRepository.findAll().stream().map(AuthorityEntity::getName).collect(Collectors.toList());
	}

	@Override
	public boolean isUserWithEmailExists(String email) {
		Optional<UserEntity> user = userRepository.findOneByEmailIgnoreCase(email);
		if (!user.isPresent()) {
			throw new ObjectNotFoundException("Nie znaleziono użytkownika");
		} else if (user.get().getEmail().equals(email)) {
			return true;
		}
		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<UserVM> getAllUsersFiltered(Pageable pageable, UserFilterVM filter) {
		Specification<UserEntity> specifications = SpecificationBuilder.getInstance().getSpecification(filter);
		if (nonNull(specifications)) {
			return userRepository.findAll(specifications, pageable).map(userMapper::toVm);
		}
		return userRepository.findAll(pageable).map(userMapper::toVm);
	}

	@Override
	public Optional<UserAvatarVM> getAvatar(Long userId) {
		UserEntity user = userRepository.findOneById(userId).orElseThrow(() -> new ObjectNotFoundException("Użytkownik z tym id nie istnieje"));
		UserAvatarVM userAvatarVM = new UserAvatarVM();
		userAvatarVM.setImage(user.getAvatar());
		return Optional.of(userAvatarVM);
	}

    @Override
    public boolean checkAdmin(String login) {
        Optional<UserEntity> userEntity = userRepository.findOneByLogin(login);
        AuthorityEntity checkAu = new AuthorityEntity();
        checkAu.setName("ROLE_ADMIN");
        if( userEntity.get().getAuthorities().contains(checkAu))
	    return true;
	    else{
            return false;
        }
    }

    @Override
    public List<UserEntity> getAllAdmins() {
        List<UserEntity> listUser = userRepository.findAll();
        UserEntity current = new UserEntity();
        AuthorityEntity checkAu = new AuthorityEntity();
        checkAu.setName("ROLE_ADMIN");
        List<UserEntity> adminList = new ArrayList<>();
        for (int i=0; i < listUser.size(); i++) {
          current = listUser.get(i);
            if( current.getAuthorities().contains(checkAu))
                adminList.add(current);
        }
        return adminList;
    }


    private void clearUserCaches(UserEntity user) {
		Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
		Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
	}
}
