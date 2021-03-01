package pl.com.tt.alpha.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import pl.com.tt.alpha.security.SecurityUtils;
import pl.com.tt.alpha.security.vm.TokenDetailsVM;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TokenStorage {

	private static final Map<String, TokenDetailsVM> tokenStorageMap = new ConcurrentHashMap<>();
	private final TokenProvider tokenProvider;

	public void putTokenDetailsToStorage(String jwt, Authentication authentication) {
		TokenDetailsVM tokenDetailsVM = new TokenDetailsVM();
		tokenDetailsVM.setLogin(SecurityUtils.getCurrentUserLogin().get());
		tokenDetailsVM.setAuthorities(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
		tokenStorageMap.put(jwt, tokenDetailsVM);
	}

	public void deleteDepreciatedToken(){
			tokenStorageMap.forEach((jwt, tokenDetails) -> {
				if (!tokenProvider.validateToken(jwt)) {
					tokenStorageMap.remove(jwt);
				}
			});
	}

	public void removeByToken(String jwt){
		tokenStorageMap.remove(jwt);
	}

	public void removeByLogin(String login) {
		tokenStorageMap.forEach((jwt, tokenDetails) -> {
			if(tokenDetails.getLogin().equals(login)){
				tokenStorageMap.remove(jwt);
			}
		});
	}

	public boolean containsKey(String jwt) {
		return tokenStorageMap.containsKey(jwt);
	}
}
