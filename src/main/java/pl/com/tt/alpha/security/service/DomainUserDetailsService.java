package pl.com.tt.alpha.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.com.tt.alpha.common.exception.UserNotActivatedException;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String login) {
		log.debug("Authenticating {}", login);

		if (new EmailValidator().isValid(login, null)) {
			Optional<UserEntity> userByEmailFromDatabase = userRepository.findOneWithAuthoritiesByEmail(login);
			return userByEmailFromDatabase.map(user -> createSpringSecurityUser(login, user)).orElseThrow(
				() -> new UsernameNotFoundException("UserEntity with email " + login + " was not found in the database"));
		}

		Optional<UserEntity> userByLoginFromDatabase = userRepository.findOneWithAuthoritiesByLogin(login);
		return userByLoginFromDatabase.map(user -> createSpringSecurityUser(login, user))
									  .orElseThrow(() -> new UsernameNotFoundException("UserEntity " + login + " was not found in the database"));
	}

	private org.springframework.security.core.userdetails.User createSpringSecurityUser(String login, UserEntity user) {
		if (!user.isActive()) {
			throw new UserNotActivatedException("UserEntity " + login + " was not active");
		}
		List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream().map(authority -> new SimpleGrantedAuthority(authority.getName()))
														.collect(Collectors.toList());
		return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), grantedAuthorities);
	}
}
