package pl.com.tt.alpha.security.vm;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.com.tt.alpha.common.ViewModel;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
public class TokenDetailsVM implements ViewModel {

	private String login;

	private Set<String> authorities;

}

