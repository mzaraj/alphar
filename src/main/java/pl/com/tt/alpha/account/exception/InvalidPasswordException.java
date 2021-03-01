package pl.com.tt.alpha.account.exception;

import pl.com.tt.alpha.common.exception.AbstractMsgKeyException;

import static pl.com.tt.alpha.common.error.ErrorConstants.INVALID_PASSWORD_TYPE;

public class InvalidPasswordException extends AbstractMsgKeyException {

	public InvalidPasswordException() {
		super("Incorrect password", INVALID_PASSWORD_TYPE);
	}

	public InvalidPasswordException(String message) {
		super(message, INVALID_PASSWORD_TYPE);
	}
}
