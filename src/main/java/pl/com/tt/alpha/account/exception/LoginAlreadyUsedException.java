package pl.com.tt.alpha.account.exception;

import pl.com.tt.alpha.common.exception.AbstractMsgKeyException;

import static pl.com.tt.alpha.common.error.ErrorConstants.LOGIN_ALREADY_USED_TYPE;

public class LoginAlreadyUsedException extends AbstractMsgKeyException {

	public LoginAlreadyUsedException() {
		super("Login is already used!", LOGIN_ALREADY_USED_TYPE);
	}
}
