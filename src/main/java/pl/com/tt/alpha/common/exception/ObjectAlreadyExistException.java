package pl.com.tt.alpha.common.exception;

public class ObjectAlreadyExistException extends RuntimeException {

	public ObjectAlreadyExistException(String message) {
		super(message);
	}

	public ObjectAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}
}
