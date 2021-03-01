package pl.com.tt.alpha.common.exception;

import lombok.Getter;

public abstract class AbstractMsgKeyException extends RuntimeException {

	@Getter
	protected String msgKey;

	public AbstractMsgKeyException(String message, String msgKey) {
		super(message);
		this.msgKey = msgKey;
	}
}
