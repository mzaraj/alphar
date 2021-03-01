package pl.com.tt.alpha.common.exception;

public class ObjectValidationException extends AbstractMsgKeyException {

	public ObjectValidationException(String msgKey, String message) {
		super(message, msgKey);
	}
}
