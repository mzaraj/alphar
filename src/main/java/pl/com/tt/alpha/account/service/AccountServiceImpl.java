package pl.com.tt.alpha.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.tt.alpha.account.exception.InvalidPasswordException;
import pl.com.tt.alpha.account.vm.KeyAndPasswordVM;
import pl.com.tt.alpha.audit.enums.AuditEnumsUser;
import pl.com.tt.alpha.audit.service.AuditEventService;
import pl.com.tt.alpha.common.exception.ObjectNotFoundException;
import pl.com.tt.alpha.common.exception.ObjectValidationException;
import pl.com.tt.alpha.common.helper.RandomHelper;
import pl.com.tt.alpha.security.SecurityUtils;
import pl.com.tt.alpha.security.jwt.TokenStorage;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.repository.UserRepository;
import pl.com.tt.alpha.user.service.UserService;
import pl.com.tt.alpha.user.vm.ResetPasswordDetailsVM;

import java.time.Instant;
import java.util.Optional;

//import pl.com.tt.alpha.captcha.service.CaptchaService;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;
	private final TokenStorage tokenStorage;

	private final AuditEventService auditEventService;
	private final UserService userService;

	@Override
	public UserEntity activateRegistration(KeyAndPasswordVM keyAndPasswordVM) {
		log.debug("Activating user for activation key {}", keyAndPasswordVM.getKey());
		UserEntity user = checkActivationKeyIsCorrect(keyAndPasswordVM.getKey());
		user.setActive(true);
		user.setActivationKey(null);
		user.setPassword(passwordEncoder.encode(keyAndPasswordVM.getNewPassword()));
		userRepository.save(user);
		auditEventService.saveAudit(SecurityUtils.getCurrentUserOnlyLogin(), user.getId(), AuditEnumsUser.ACTIVATE_USER_ACCOUNT);
		log.debug("Activated user: {}", user);
		return user;
	}

	@Override
	public UserEntity checkActivationKeyIsCorrect(String key) {
		log.debug("Checking user for activation key {}", key);
		UserEntity user = userRepository.findOneByActivationKey(key)
										.orElseThrow(() -> new ObjectNotFoundException("NNie znaleziono użytkownika z tym kode aktywacyjnym"));
		Long keyExpiration = Long.valueOf(user.getActivationKey().substring(6));
		if (Instant.now().isAfter(Instant.ofEpochMilli(keyExpiration))) {
			user.setActivationKey(null);
			userRepository.save(user);
			throw new ObjectValidationException("activationKeyExpired", "Activation key has expired");
		}
		return user;
	}

	@Override
	public UserEntity deactivateUser(final String login) {
		UserEntity user = userRepository.findOneByLogin(login).orElseThrow(() -> new ObjectNotFoundException("Użytkownik z tym loginem nie istnieje"));
		user.setActive(false);
		user.setActivationKey(null);
		auditEventService.saveAudit(SecurityUtils.getCurrentUserOnlyLogin(), user.getId(), AuditEnumsUser.DEACTIVATE_USER);
		userRepository.save(user);
		log.debug("Deactivated user: {}", user.getLogin());
		return user;
	}

	@Override
	public UserEntity completePasswordReset(String newPassword, String key) {
		log.debug("Reset user password for reset key {}", key);

		return userRepository.findOneByResetKey(key).filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400))).map(user -> {
			user.setPassword(passwordEncoder.encode(newPassword));
			user.setResetKey(null);
			user.setResetDate(null);
			return user;
		}).orElseThrow(() -> new ObjectNotFoundException("No user was found for this reset key"));
	}

	@Override
	public Optional<UserEntity> requestPasswordReset(ResetPasswordDetailsVM resetPasswordDetails) {
		boolean isCorrectLoginAndEmail = userService.isUserWithEmailExists( resetPasswordDetails.getEmail());
		if (!isCorrectLoginAndEmail) {
			throw new ObjectNotFoundException("Użytkownik z tym loginem oraz nazwiskie nie istnieje");
		}
		return userRepository.findOneByEmailIgnoreCase(resetPasswordDetails.getEmail()).filter(UserEntity::isActive).map(user -> {
			user.setResetKey(RandomHelper.generateResetKey());
			user.setResetDate(Instant.now());
			return user;
		});
	}

	@Override
	public void changePassword(String currentClearTextPassword, String newPassword) {
		SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(user -> {
			String currentEncryptedPassword = user.getPassword();
			if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
				throw new InvalidPasswordException();
			}
			String encryptedPassword = passwordEncoder.encode(newPassword);
			user.setPassword(encryptedPassword);
			auditEventService.saveAudit(SecurityUtils.getCurrentUserOnlyLogin(), user.getId(), AuditEnumsUser.CHANGE_PASSWORD);
			log.debug("Changed password for UserEntity: {}", user);
		});
	}

	@Override
	public void logOut() {
		if (SecurityUtils.isAuthenticated() && SecurityUtils.getCurrentUserJWT().isPresent()) {
			tokenStorage.removeByToken(SecurityUtils.getCurrentUserJWT().get());
		} else
			log.debug("Cannot logout. User is not authenticated.");
	}

	@Override
	public void logOut(String login) {
		tokenStorage.removeByLogin(login);
	}
}
