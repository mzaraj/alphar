package pl.com.tt.alpha.user.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.com.tt.alpha.common.Mapper;
import pl.com.tt.alpha.common.helper.RandomHelper;
import pl.com.tt.alpha.user.domain.AuthorityEntity;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.repository.AuthorityRepository;
import pl.com.tt.alpha.user.repository.UserRepository;
import pl.com.tt.alpha.user.vm.UserVM;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pl.com.tt.alpha.configuration.Constants.DEFAULT_LANGUAGE;

@Component
@RequiredArgsConstructor
public class UserMapper implements Mapper<UserEntity, UserVM> {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthorityRepository authorityRepository;

	@Override
	public UserEntity toEntity(UserVM userVM) {
		UserEntity user = getUserEntity(userVM.getId());
		user.setFirstName(userVM.getFirstName());
		user.setLastName(userVM.getLastName());
		user.setEmail(userVM.getEmail());
		user.setActive(userVM.isActive());
		user.setLangKey(userVM.getLangKey());
		user.setCity(userVM.getCity());
		user.setBday(userVM.getBday());
		if (isNull(userVM.getLangKey())) {
			user.setLangKey(DEFAULT_LANGUAGE);
		}
		Set<AuthorityEntity> authorities = authoritiesFromStrings(userVM);
		if (nonNull(authorities)) {
			user.setAuthorities(authorities);
		}
		return user;
	}

	private UserEntity getUserEntity(Long id) {
		if (Objects.nonNull(id)) {
			return userRepository.getOne(id);
		}
		return new UserEntity();
	}

	public Set<AuthorityEntity> authoritiesFromStrings(UserVM userVM) {
		return userVM.getAuthorities().stream()
					 .map(authorityRepository::findById)
					 .filter(Optional::isPresent).map(Optional::get)
					 .collect(Collectors.toSet());
	}

	@Override
	public UserVM toVm(UserEntity entity) {
		UserVM userVM = new UserVM();
		userVM.setId(entity.getId());
		userVM.setLogin(entity.getLogin());
		userVM.setFirstName(entity.getFirstName());
		userVM.setLastName(entity.getLastName());
		userVM.setEmail(entity.getEmail());
		userVM.setActive(entity.isActive());
		userVM.setCity(entity.getCity());
		userVM.setBday(entity.getBday());
		userVM.setLangKey(entity.getLangKey());
		userVM.setAuthorities(entity.getAuthorities().stream().map(AuthorityEntity::getName).collect(Collectors.toSet()));

		return userVM;
	}

	public UserEntity createNewUser(UserVM userVM) {
		UserEntity user = toEntity(userVM);
		user.setLogin(userVM.getLogin().toLowerCase());
		String encryptedPassword = passwordEncoder.encode(RandomHelper.generatePassword());
		user.setPassword(encryptedPassword);
		user.setActivationKey(RandomHelper.generateActivationKey());
		user.setResetKey(RandomHelper.generateResetKey());
		user.setResetDate(Instant.now());

		return user;
	}

}
