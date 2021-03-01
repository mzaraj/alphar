package pl.com.tt.alpha.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import pl.com.tt.alpha.common.exception.UserNotActivatedException;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.repository.UserRepository;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtils {

	/**
	 * Get the login of the current user.
	 *
	 * @return the login of the current user
	 */
	public static Optional<String> getCurrentUserLogin() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		return Optional.ofNullable(securityContext.getAuthentication()).map(authentication -> {
			if (authentication.getPrincipal() instanceof UserDetails) {
				UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
				return springSecurityUser.getUsername();
			} else if (authentication.getPrincipal() instanceof String) {
				return (String) authentication.getPrincipal();
			}
			return null;
		});
	}

	public static UserEntity getCurrentUser(UserRepository userRepository) {
		return getCurrentUserLogin().flatMap(userRepository::findOneByLogin)
									.orElseThrow(() -> new UserNotActivatedException("Dostęp do zasobu wymaga zalogowania"));
	}

	public static String getCurrentUserOnlyLogin() {
		return getCurrentUserLogin().orElseThrow(() -> new UserNotActivatedException("Dostęp do zasobu wymaga zalogowania"));
	}

	/**
	 * Get the JWT of the current user.
	 *
	 * @return the JWT of the current user
	 */
	public static Optional<String> getCurrentUserJWT() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		return Optional.ofNullable(securityContext.getAuthentication()).filter(authentication -> authentication.getCredentials() instanceof String)
					   .map(authentication -> (String) authentication.getCredentials());
	}

	/**
	 * Check if a user is authenticated.
	 *
	 * @return true if the user is authenticated, false otherwise
	 */
	public static boolean isAuthenticated() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		return Optional.ofNullable(securityContext.getAuthentication()).map(authentication -> authentication.getAuthorities().stream().noneMatch(
			grantedAuthority -> grantedAuthority.getAuthority().equals(AuthoritiesConstants.ANONYMOUS))).orElse(false);
	}

	/**
	 * If the current user has a specific authority (security role).
	 * <p>
	 * The name of this method comes from the isUserInRole() method in the Servlet API
	 *
	 * @param authority the authority to check
	 * @return true if the current user has the authority, false otherwise
	 */
	public static boolean isCurrentUserInRole(String authority) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		return Optional.ofNullable(securityContext.getAuthentication()).map(
			authentication -> authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority)))
					   .orElse(false);
	}
}
