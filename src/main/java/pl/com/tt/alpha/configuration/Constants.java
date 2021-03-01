package pl.com.tt.alpha.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

	// Regex for acceptable logins
	public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";

	public static final String SYSTEM_ACCOUNT = "system";
	public static final String ANONYMOUS_USER = "anonymoususer";
	public static final String DEFAULT_LANGUAGE = "pl";

	public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
}
