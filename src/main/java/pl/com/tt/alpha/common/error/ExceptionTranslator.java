package pl.com.tt.alpha.common.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pl.com.tt.alpha.common.error.vm.ErrorVM;
import pl.com.tt.alpha.common.exception.AbstractMsgKeyException;
import pl.com.tt.alpha.common.exception.CustomParameterizedException;
import pl.com.tt.alpha.common.exception.ObjectAlreadyExistException;
import pl.com.tt.alpha.common.exception.ObjectNotFoundException;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.*;
import static pl.com.tt.alpha.common.error.ErrorConstants.*;
import static pl.com.tt.alpha.security.SecurityUtils.getCurrentUserLogin;
import static pl.com.tt.alpha.security.SecurityUtils.isAuthenticated;

@Slf4j
@ControllerAdvice
public class ExceptionTranslator {

	private static final String NO_MESSAGE_AVAILABLE = "(no message available)";
	private static final String UNSPECIFIED = "(unspecified)";

	private String getMessage(Exception ex) {
		return nonNull(ex.getMessage()) ? ex.getMessage() : NO_MESSAGE_AVAILABLE;
	}

	private String getCause(Exception ex) {
		return nonNull(ex.getCause()) ? String.valueOf(ex.getCause()) : UNSPECIFIED;
	}

	protected void logError(String method, Exception ex) {
		if (isAuthenticated()) {
			log.error("logError() for user: {}", getCurrentUserLogin());
		} else {
			log.error("logError() for unathenticated");
		}
		log.error("{}({}) with cause: {} and exception: {}", method, ex.getClass(), getCause(ex), getMessage(ex));
		log.error("Exception ", ex);
	}

	@ResponseBody
	@ExceptionHandler(AbstractMsgKeyException.class)
	public ResponseEntity<?> processMsgKeyException(AbstractMsgKeyException ex) {
		logError("processMsgKeyException", ex);
		return ResponseEntity.badRequest().body(new ErrorVM(ex.getMsgKey(), ex.getLocalizedMessage()));
	}


	@ResponseBody
	@ExceptionHandler(CustomParameterizedException.class)
	public ResponseEntity<?> processParameterizedValidationError(CustomParameterizedException ex) {
		logError("processParameterizedValidationError", ex);
		return ResponseEntity.badRequest().body(ex.getErrorVM());
	}

	@ResponseBody
	@ExceptionHandler(IOException.class)
	public ResponseEntity<?> IOException(IOException ex) {
		logError("inputOutputExceptionError", ex);
		return new ResponseEntity<>(new ErrorVM(ERR_INPUT_OUTPUT_OBJECT), BAD_REQUEST);
	}

	@ResponseBody
	@ExceptionHandler(ObjectAlreadyExistException.class)
	public ResponseEntity<?> objectAlreadyExistException(ObjectAlreadyExistException ex) {
		logError("objectAlreadyExistError", ex);
		return new ResponseEntity<>(new ErrorVM(ERR_OBJECT_ALREADY_EXIST, ex.getLocalizedMessage()), CONFLICT);
	}

	@ResponseBody
	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<?> objectNotFoundException(ObjectNotFoundException ex) {
		logError("objectNotFoundError", ex);
		return new ResponseEntity<>(new ErrorVM(ERR_OBJECT_NOT_FOUND, ex.getLocalizedMessage()), NOT_FOUND);
	}

	@ResponseBody
	@ExceptionHandler(ConcurrencyFailureException.class)
	public ResponseEntity<?> processConcurrencyError(ConcurrencyFailureException ex) {
		logError("processConcurrencyError", ex);
		return new ResponseEntity<>(new ErrorVM(ERR_CONCURRENCY_FAILURE), CONFLICT);
	}

	@ResponseBody
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<?> processAccessDeniedException(AccessDeniedException ex) {
		logError("processAccessDeniedException", ex);
		return new ResponseEntity<>(new ErrorVM(ERR_ACCESS_DENIED, ex.getLocalizedMessage()), FORBIDDEN);
	}

	@ResponseBody
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<?> processAuthenticationException(AuthenticationException ex) {
		logError("processAuthenticationException", ex);
		return new ResponseEntity<>(new ErrorVM(ERR_ACCESS_DENIED), UNAUTHORIZED);
	}

	@ResponseBody
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<?> processMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
		logError("processMethodNotSupportedException", ex);
		return new ResponseEntity<>(new ErrorVM(ERR_METHOD_NOT_SUPPORTED), METHOD_NOT_ALLOWED);
	}

	@ResponseBody
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> processMessageNotReadableException(HttpMessageNotReadableException ex) {
		logError("processMessageNotReadableException", ex);
		return new ResponseEntity<>(new ErrorVM(ERR_MESSAGE_NOT_READABLE), BAD_REQUEST);
	}

	@ResponseBody
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<?> processCannotDeleteRelatedObject(DataIntegrityViolationException ex) {
		logError("processCannotDeleteRelatedObject", ex);
		return ResponseEntity.badRequest().body(new ErrorVM(ERR_CANNOT_DELETE_RELATED_OBJECT));
	}

	@ResponseBody
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> processValidationError(MethodArgumentNotValidException ex) {
		logError("processValidationError", ex);
		BindingResult result = ex.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();
		ErrorVM vm = new ErrorVM(ErrorConstants.ERR_VALIDATION);
		for (FieldError fieldError : fieldErrors) {
			vm.add(fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
		}
		return new ResponseEntity<>(vm, HttpStatus.BAD_REQUEST);
	}

	@ResponseBody
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<?> methodArgumentValidationError(MethodArgumentTypeMismatchException ex) {
		logError("methodArgumentValidationError", ex);
		return ResponseEntity.badRequest().body(new ErrorVM(ERR_METHOD_ARGUMENT_TYPE_MISMATCH, ex.getLocalizedMessage()));
	}

	@ResponseBody
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<?> missingServletRequestParameterException(MissingServletRequestParameterException ex) {
		logError("missingServletRequestParameterException", ex);
		return ResponseEntity.badRequest().body(new ErrorVM(ERR_MISSING_SERVLET_REQUEST_PARAMETER, ex.getLocalizedMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorVM> processException(Exception ex) {
		logError("processException", ex);
		return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ErrorVM(ERR_INTERNAL_SERVER_ERROR));
	}

}
