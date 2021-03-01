package pl.com.tt.alpha.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JWTConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	public static final String AUTHORIZATION_HEADER = "Authorization";

	private final TokenProvider tokenProvider;
	private final TokenStorage tokenStorage;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		JWTFilter customFilter = new JWTFilter(tokenProvider, tokenStorage);
		http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
