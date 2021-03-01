package pl.com.tt.alpha.security.jwt;

import io.github.jhipster.config.JHipsterProperties;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import pl.com.tt.alpha.security.AuthoritiesConstants;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

	private static final String AUTHORITIES_KEY = "auth";
	private final Base64.Encoder encoder = Base64.getEncoder();

	private String secretKey;
	private long tokenValidityInMilliseconds;
	private final JHipsterProperties jHipsterProperties;
	private Date validity;

	@PostConstruct
	public void init() {
		this.secretKey = encoder.encodeToString(jHipsterProperties.getSecurity().getAuthentication().getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
		this.tokenValidityInMilliseconds = 1000 * jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
	}

	public String createToken(Authentication authentication) {
		String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
		if(authorities.isEmpty()){
			authorities = AuthoritiesConstants.USER;
		}

		long now = (new Date()).getTime();
		validity = new Date(now + this.tokenValidityInMilliseconds);

		return Jwts.builder().setSubject(authentication.getName()).claim(AUTHORITIES_KEY, authorities).signWith(SignatureAlgorithm.HS512, secretKey)
				   .setExpiration(validity).compact();
	}

	public Authentication getAuthentication(String token) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		Collection authorities = Arrays.stream(claims.get(AUTHORITIES_KEY)
													 .toString()
													 .split(","))
									   .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

		User principal = new User(claims.getSubject(), "", authorities);

		return new UsernamePasswordAuthenticationToken(principal, token, authorities);
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			log.info("Invalid JWT signature.");
			log.trace("Invalid JWT signature trace: {}", e);
		} catch (MalformedJwtException e) {
			log.info("Invalid JWT token.");
			log.trace("Invalid JWT token trace: {}", e);
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT token.");
			log.trace("Expired JWT token trace: {}", e);
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT token.");
			log.trace("Unsupported JWT token trace: {}", e);
		} catch (IllegalArgumentException e) {
			log.info("JWT token compact of handler are invalid.");
			log.trace("JWT token compact of handler are invalid trace: {}", e);
		}
		return false;
	}
}
