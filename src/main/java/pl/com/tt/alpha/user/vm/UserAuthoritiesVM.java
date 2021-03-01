package pl.com.tt.alpha.user.vm;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.com.tt.alpha.common.ViewModel;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserAuthoritiesVM implements ViewModel {
	@NotNull
	private Set<String> authorities;
}
