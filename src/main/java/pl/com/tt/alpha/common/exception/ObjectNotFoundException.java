package pl.com.tt.alpha.common.exception;

public class ObjectNotFoundException extends RuntimeException {

	public ObjectNotFoundException(String message) {
		super(message);
	}

	public ObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
