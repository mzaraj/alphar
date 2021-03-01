package pl.com.tt.alpha.authentication.resource;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.com.tt.alpha.account.service.AccountService;
import pl.com.tt.alpha.authentication.AuthenticationAttemptsCounter;
import pl.com.tt.alpha.authentication.vm.LoginVM;
import pl.com.tt.alpha.security.jwt.JWTConfigurer;
import pl.com.tt.alpha.security.jwt.TokenProvider;
import pl.com.tt.alpha.security.jwt.TokenStorage;
import pl.com.tt.alpha.user.repository.UserRepository;
import pl.com.tt.alpha.user.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class JWTResource {

	private final TokenProvider tokenProvider;
	private final AuthenticationManager authenticationManager;
	private final AuthenticationAttemptsCounter authenticationAttemptsCounter;
	private final TokenStorage tokenStorage;
	private final AccountService accountService;
	private final UserService userService;

	@Timed
	@PostMapping("/authenticate")
	public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
		loginVM.setUsername(loginVM.getUsername().toLowerCase());
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
		Authentication authentication;

		try {
			authentication = authenticationManager.authenticate(authenticationToken);
		} catch (AuthenticationException e) {
			authenticationAttemptsCounter.increaseIncorrectLoginCounter(loginVM);
			throw new AccessDeniedException("");
		}

		authenticationAttemptsCounter.checkLoginAttempts(loginVM);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = tokenProvider.createToken(authentication);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
		tokenStorage.putTokenDetailsToStorage(jwt, authentication);

		return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
	}

	@GetMapping(path = "/logout")
	@Timed
	public void logoutAccount() {
		accountService.logOut();
	}

	/**
	 * Object to return as body in JWT Authentication.
	 */
	static class JWTToken {

		private String idToken;

		JWTToken(String idToken) {
			this.idToken = idToken;
		}

		@JsonProperty("id_token")
		String getIdToken() {
			return idToken;
		}

		void setIdToken(String idToken) {
			this.idToken = idToken;
		}
	}
}
