package pl.com.tt.alpha.common.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorConstants {

	public static final String ERR_INTERNAL_SERVER_ERROR = "error.internalServerError";
	public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
	public static final String ERR_ACCESS_DENIED = "error.accessDenied";
	public static final String ERR_METHOD_NOT_SUPPORTED = "error.methodNotSupported";
	public static final String ERR_METHOD_ARGUMENT_TYPE_MISMATCH = "error.methodArgumentTypeMismatch";
	public static final String ERR_MISSING_SERVLET_REQUEST_PARAMETER = "error.missingServletRequestParameterException";
	public static final String ERR_MESSAGE_NOT_READABLE = "error.messageNotReadable";
	public static final String ERR_OBJECT_ALREADY_EXIST = "error.objectAlreadyExist";
	public static final String ERR_INPUT_OUTPUT_OBJECT = "error.inputOutputObject";
	public static final String ERR_OBJECT_NOT_FOUND = "error.objectNotFound";
	public static final String ERR_CANNOT_DELETE_RELATED_OBJECT = "error.cannotDeleteRelatedObject";
	public static final String ERR_VALIDATION = "error.validation";

	public static final String LOGIN_ALREADY_USED_TYPE = "error.user.loginExists";
	public static final String EMAIL_ALREADY_USED_TYPE = "error.user.emailExists";
	public static final String EMAIL_NOT_FOUND_TYPE = "error.user.emailNotFound";
	public static final String INVALID_PASSWORD_TYPE = "error.user.invalidPassword";

	public static final String SUBCATEGORY_HAS_PARENT_TYPE = "error.subcategory.hasParent";
	public static final String USER_NOT_ALLOWED = "error.user.notAllowed";
}
