package pl.com.tt.alpha.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenStorageScheduler {

	private final TokenStorage tokenStorage;

	@Scheduled(fixedRate = 60000)
	public void deleteDepreciatedTokenSheduled() {
		tokenStorage.deleteDepreciatedToken();
	}
}
