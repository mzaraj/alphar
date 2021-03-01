package pl.com.tt.alpha.common.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeaderHelper {

	private static final String APPLICATION_NAME = "alphaApp";

	public static HttpHeaders createAlert(String message, String param) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-alphaApp-alert", message);
		headers.add("X-alphaApp-params", param);
		return headers;
	}

	public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
		return createAlert(APPLICATION_NAME + "." + entityName + ".created", param);
	}

	public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
		return createAlert(APPLICATION_NAME + "." + entityName + ".updated", param);
	}

	public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
		return createAlert(APPLICATION_NAME + "." + entityName + ".deleted", param);
	}

	public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
		log.error("Entity processing failed, {}", defaultMessage);
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-alphaApp-error", "error." + errorKey);
		headers.add("X-alphaApp-params", entityName);
		return headers;
	}
}
