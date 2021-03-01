package pl.com.tt.alpha.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.com.tt.alpha.authentication.vm.LoginVM;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Transactional
public class AuthenticationAttemptsCounter {

	private final UserRepository userRepository;

	@Transactional(noRollbackFor = AccessDeniedException.class)
	public void increaseIncorrectLoginCounter(LoginVM loginVM) {
		UserEntity userEntity = userRepository.findOneByLogin(loginVM.getUsername()).orElseThrow(() -> new AccessDeniedException(""));
		userRepository.save(userEntity);
	}

	public void checkLoginAttempts(LoginVM loginVM) {
		UserEntity userEntity = userRepository.findOneByLogin(loginVM.getUsername()).orElseThrow(() -> new AccessDeniedException(""));
		userRepository.save(userEntity);
	}
}
