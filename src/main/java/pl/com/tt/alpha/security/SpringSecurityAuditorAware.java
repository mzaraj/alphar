package pl.com.tt.alpha.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import pl.com.tt.alpha.configuration.Constants;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		return Optional.of(SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT));
	}
}
