package pl.com.tt.alpha.account.exception;

import pl.com.tt.alpha.common.exception.AbstractMsgKeyException;

import static pl.com.tt.alpha.common.error.ErrorConstants.EMAIL_NOT_FOUND_TYPE;

public class EmailNotFoundException extends AbstractMsgKeyException {

	public EmailNotFoundException() {
		super("Email address not registered", EMAIL_NOT_FOUND_TYPE);
	}
}
