package pl.com.tt.alpha.user.resource;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.com.tt.alpha.account.exception.EmailAlreadyUsedException;
import pl.com.tt.alpha.account.exception.LoginAlreadyUsedException;
import pl.com.tt.alpha.common.helper.HeaderHelper;
import pl.com.tt.alpha.common.helper.PaginationHelper;
import pl.com.tt.alpha.configuration.Constants;
import pl.com.tt.alpha.events.domain.EventMembersEntity;
import pl.com.tt.alpha.mail.MailService;
import pl.com.tt.alpha.security.AuthoritiesConstants;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.mapper.UserAuthoritiesMapper;
import pl.com.tt.alpha.user.mapper.UserMapper;
import pl.com.tt.alpha.user.repository.UserRepository;
import pl.com.tt.alpha.user.service.UserService;
import pl.com.tt.alpha.user.vm.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserResource {

	private final UserService userService;
	private final UserRepository userRepository;
	private final MailService mailService;
	private final UserMapper userMapper;
	private final UserAuthoritiesMapper userAuthoritiesMapper;

	/**
	 * POST  /users  : Creates a new user.
	 * <p>
	 * Creates a new user if the login and email are not already used, and sends an
	 * mail with an activation link.
	 * The user needs to be active on creation.
	 *
	 * @param userVM the user to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new user, or with status 400 (Bad Request) if the login or email is already in use
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PostMapping
	@Timed
	@Secured({AuthoritiesConstants.USERLOG,AuthoritiesConstants.ADMIN})
	public ResponseEntity<UserEntity> createUser(@Valid @RequestBody UserVM userVM) throws URISyntaxException {
		log.debug("REST request to save UserEntity : {}", userVM);
		if (userRepository.findOneByLogin(userVM.getLogin().toLowerCase()).isPresent()) {
			throw new LoginAlreadyUsedException();
		} else if (userRepository.findOneByEmailIgnoreCase(userVM.getEmail()).isPresent()) {
			throw new EmailAlreadyUsedException();
		} else {
			UserEntity newUser = userService.createUser(userVM);
			mailService.sendActivationEmail(newUser);
			return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
								 .headers(HeaderHelper.createAlert("userManagement.created", newUser.getLogin())).body(newUser);
		}
	}

	@PutMapping("/avatar")
	public ResponseEntity<UserVM> addAvatarToUser(@RequestParam("file") MultipartFile file) throws URISyntaxException,
		IOException {
		UserVM user = userService.addAvatarToUser(file);
		return ResponseEntity.created(new URI("/api/users/" + user.getLogin())).headers(HeaderHelper.createAlert("avatar.added", file.getName())).body(user);
	}

	@GetMapping("/avatar/{userId}")
	public ResponseEntity<UserAvatarVM> getAvatar(@PathVariable(value = "userId") Long userId) {
		Optional<UserAvatarVM> avatar = userService.getAvatar(userId);
		return ResponseUtil.wrapOrNotFound(avatar, HeaderHelper.createAlert("avatar.getId", userId.toString()));
	}
	/**
	 * PUT /users : Updates an existing UserEntity.
	 *
	 * @param userVM the user to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated user
	 * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already in use
	 * @throws LoginAlreadyUsedException 400 (Bad Request) if the login is already in use
	 */
	@PutMapping
	@Timed
	@Secured({AuthoritiesConstants.USER,AuthoritiesConstants.ADMIN})
	public ResponseEntity<UserVM> updateUser(@Valid @RequestBody UserVM userVM) {
		log.debug("REST request to update UserEntity : {}", userVM);
		Optional<UserEntity> existingUser = userRepository.findOneByEmailIgnoreCase(userVM.getEmail());
		if (existingUser.isPresent() && (!existingUser.get().getId().equals(userVM.getId()))) {
			throw new EmailAlreadyUsedException();
		}
		existingUser = userRepository.findOneByLogin(userVM.getLogin().toLowerCase());
		if (existingUser.isPresent() && (!existingUser.get().getId().equals(userVM.getId()))) {
			throw new LoginAlreadyUsedException();
		}
		Optional<UserVM> updatedUser = userService.updateUser(userVM);

		return ResponseUtil.wrapOrNotFound(updatedUser, HeaderHelper.createAlert("userManagement.updated", userVM.getLogin()));
	}

	/**
	 * GET /users : get all users.
	 *
         * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and with body all users
	 */
    @GetMapping
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<UserVM>> getAllUsers(Pageable pageable) {
        final Page<UserVM> page = userService.getAllManagedUsers(pageable);
        HttpHeaders headers = PaginationHelper.generatePaginationHttpHeaders(page, "/api/users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/filtered")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<UserVM>> getAllFiltered(Pageable pageable, @ApiParam UserFilterVM userFilterVM) {
        final Page<UserVM> page = userService.getAllUsersFiltered(pageable, userFilterVM);
        HttpHeaders headers = PaginationHelper.generatePaginationHttpHeaders(page, "/api/users/filtered");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * @return a string list of the all of the roles
     */
	@GetMapping("/authorities")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public List<String> getAuthorities() {
		return userService.getAuthorities();
	}

    @GetMapping("/check/{login}")
    @Timed
    public boolean checkAdmin(@PathVariable String login)
    {
        return userService.checkAdmin(login);
    }

	/**
	 * GET /users/:login : get the "login" user.
	 *
	 * @param login the login of the user to find
	 * @return the ResponseEntity with status 200 (OK) and with body the "login" user, or with status 404 (Not Found)
	 */
	@GetMapping("/{login:" + Constants.LOGIN_REGEX + "}")
	@Timed
	public ResponseEntity<UserVM> getUser(@PathVariable String login) {
		log.debug("REST request to get UserEntity : {}", login);
		return ResponseUtil.wrapOrNotFound(userService.getUserWithAuthoritiesByLogin(login).map(userMapper::toVm));
	}

    @GetMapping("/get/id/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    public Long getUserId(@PathVariable String login) {
        log.debug("REST request to get UserId : {}", login);
        return userService.getUserId(login);
    }

	@GetMapping("/{login:" + Constants.LOGIN_REGEX + "}/authorities")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<UserAuthoritiesVM> getUserAuthorities(@PathVariable String login) {
		log.debug("REST request to get UserEntity : {}", login);
		return ResponseUtil.wrapOrNotFound(userService.getUserWithAuthoritiesByLogin(login).map(userAuthoritiesMapper::toVm));
	}

	@PutMapping("/{login:" + Constants.LOGIN_REGEX + "}/authorities")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<UserAuthoritiesVM> setUserAuthorities(@PathVariable String login, @Valid @RequestBody UserAuthoritiesVM userAuthoritiesVM) {
		Optional<UserAuthoritiesVM> updatedUser = userService.updateUserAuthorities(login, userAuthoritiesVM);
		log.debug("REST request to put UserEntity : {} authorities", login);
		return ResponseUtil.wrapOrNotFound(updatedUser, HeaderHelper.createAlert("userAuthorities.updated", login));
	}

	/**
	 * DELETE /users/:login : delete the "login" UserEntity.
	 *
	 * @param login the login of the user to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@DeleteMapping("/{login:" + Constants.LOGIN_REGEX + "}")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<Void> deleteUser(@PathVariable String login) {
		log.debug("REST request to delete UserEntity: {}", login);
		userService.deleteUser(login);
		return ResponseEntity.ok().headers(HeaderHelper.createAlert("userManagement.deleted", login)).build();
	}

    @GetMapping("/admins")
    public ResponseEntity<List<UserEntity>> getAllAdmins() {
        final List<UserEntity> list = userService.getAllAdmins();
        return new ResponseEntity<>( list, HttpStatus.OK);
    }
}
