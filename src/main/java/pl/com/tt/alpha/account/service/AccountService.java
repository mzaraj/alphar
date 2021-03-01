package pl.com.tt.alpha.account.service;

import pl.com.tt.alpha.account.vm.KeyAndPasswordVM;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.vm.ResetPasswordDetailsVM;

import java.util.Optional;

public interface AccountService {

	UserEntity activateRegistration(KeyAndPasswordVM keyAndPasswordVM);

	UserEntity checkActivationKeyIsCorrect(String key);

	UserEntity deactivateUser(String login);

	UserEntity completePasswordReset(String newPassword, String key);

	Optional<UserEntity> requestPasswordReset(ResetPasswordDetailsVM resetPasswordDetails);

	void changePassword(String currentClearTextPassword, String newPassword);

	void logOut();

	void logOut(String login);
}
