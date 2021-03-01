package pl.com.tt.alpha.account.exception;

import pl.com.tt.alpha.common.exception.AbstractMsgKeyException;

import static pl.com.tt.alpha.common.error.ErrorConstants.EMAIL_ALREADY_USED_TYPE;

public class EmailAlreadyUsedException extends AbstractMsgKeyException {

	public EmailAlreadyUsedException() {
		super("Email is already in use!", EMAIL_ALREADY_USED_TYPE);
	}
}
