package pl.com.tt.alpha.authentication.vm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ToString(exclude = "password")
@EqualsAndHashCode(exclude = "password")
public class LoginVM {

	@NotNull
	@Size(min = 1, max = 50)
	private String username;

	@NotNull
	private String password;

}
