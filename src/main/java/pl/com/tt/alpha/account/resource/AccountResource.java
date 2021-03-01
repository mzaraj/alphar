package pl.com.tt.alpha.account.resource;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.com.tt.alpha.account.exception.EmailNotFoundException;
import pl.com.tt.alpha.account.service.AccountService;
import pl.com.tt.alpha.account.vm.KeyAndPasswordVM;
import pl.com.tt.alpha.account.vm.PasswordChangeVM;
import pl.com.tt.alpha.common.helper.HeaderHelper;
import pl.com.tt.alpha.configuration.Constants;
import pl.com.tt.alpha.mail.MailService;
import pl.com.tt.alpha.security.AuthoritiesConstants;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.mapper.UserMapper;
import pl.com.tt.alpha.user.repository.UserRepository;
import pl.com.tt.alpha.user.service.UserService;
import pl.com.tt.alpha.user.vm.ResetPasswordDetailsVM;
import pl.com.tt.alpha.user.vm.UserVM;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountResource {

	private final UserRepository userRepository;

	private final AccountService accountService;
	private final UserService userService;

	private final MailService mailService;
	private final UserMapper userMapper;

	@GetMapping("/activate")
	@Timed
	public ResponseEntity<Void> checkActivationKey(@RequestParam(value = "key") String key) {
		accountService.checkActivationKeyIsCorrect(key);
		return ResponseEntity.ok().headers(HeaderHelper.createAlert("activationKey.correct", key)).build();
	}


	@PostMapping("/activate")
	@Timed
	public ResponseEntity<Void> activateAccount(@Valid @RequestBody KeyAndPasswordVM keyAndPasswordVM) {
		UserEntity user = accountService.activateRegistration(keyAndPasswordVM);
		return ResponseEntity.ok().headers(HeaderHelper.createAlert("user.activated", user.getLogin())).build();
	}

	@GetMapping("/deactivate/{login:" + Constants.LOGIN_REGEX + "}")
	@Secured({AuthoritiesConstants.ADMIN,AuthoritiesConstants.USER})
	@Timed
	public ResponseEntity<Void> deactivateAccount(@PathVariable String login) {
		UserEntity user = accountService.deactivateUser(login);
		accountService.logOut(login);
		return ResponseEntity.ok().headers(HeaderHelper.createAlert("user.deactivated", user.getLogin())).build();
	}

	@GetMapping("/authenticate")
	@Timed
	public String isAuthenticated(HttpServletRequest request) {
		log.debug("REST request to check if the current user is authenticated");
		return request.getRemoteUser();
	}

	@GetMapping("/account")
	@Timed
	public ResponseEntity<UserVM> getAccount() {
		return ResponseUtil.wrapOrNotFound(userService.getUserWithAuthorities().map(userMapper::toVm));
	}

	@PostMapping(path = "/account/change-password")
	@Timed
	public void changePassword(@Valid @RequestBody PasswordChangeVM passwordChangeVM) {
		accountService.changePassword(passwordChangeVM.getCurrentPassword(), passwordChangeVM.getNewPassword());
		accountService.logOut();
	}

	@PostMapping(path = "/account/reset-password/init")
	@Timed
	public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody ResetPasswordDetailsVM resetPasswordDetails) {
		mailService.sendPasswordResetMail(accountService.requestPasswordReset(resetPasswordDetails).orElseThrow(EmailNotFoundException::new));
		return ResponseEntity.ok().headers(HeaderHelper.createAlert("passwordReset.request", resetPasswordDetails.getEmail())).build();
	}

	@PostMapping(path = "/account/reset-password/finish")
	@Timed
	public ResponseEntity<Void> finishPasswordReset(@Valid @RequestBody KeyAndPasswordVM keyAndPassword) {
		UserEntity user = accountService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());
		return ResponseEntity.ok().headers(HeaderHelper.createAlert("passwordReset.finish", user.getLogin())).build();
	}
}
