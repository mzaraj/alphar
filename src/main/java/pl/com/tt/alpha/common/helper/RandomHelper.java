package pl.com.tt.alpha.common.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomHelper {

	private static final int DEF_COUNT = 20;

	/**
	 * Generate a password.
	 *
	 * @return the generated password
	 */
	public static String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
	}

	/**
	 * Generate an activation key.
	 *
	 * @return the generated activation key
	 */
	public static String generateActivationKey() {
		return RandomStringUtils.randomNumeric(6) + Instant.now().plusSeconds(86400).toEpochMilli();
	}

	/**
	 * Generate a reset key.
	 *
	 * @return the generated reset key
	 */
	public static String generateResetKey() {
		return RandomStringUtils.randomNumeric(DEF_COUNT);
	}
}
