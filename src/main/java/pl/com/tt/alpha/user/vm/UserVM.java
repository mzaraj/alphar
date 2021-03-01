package pl.com.tt.alpha.user.vm;

import lombok.*;
import pl.com.tt.alpha.common.ViewModel;
import pl.com.tt.alpha.configuration.Constants;

import javax.persistence.Column;
import javax.validation.constraints.*;
import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVM implements ViewModel {

	private Long id;

	@NotBlank
	@Pattern(regexp = Constants.LOGIN_REGEX)
	@Size(min = 4, max = 50)
	private String login;

	@Size(max = 50)
	private String firstName;

	@Size(max = 50)
	private String lastName;

    @Size(min = 2, max = 50)
    private String city;

    private Instant bday;

	@Email
	@Size(min = 5, max = 254)
	private String email;

	@NotNull
	private boolean active;

	private String langKey;

	private String createdBy;

	private Instant createdDate;

	private String lastModifiedBy;

	private Instant lastModifiedDate;

	@NotNull
	private Set<String> authorities;

//	private boolean ldap;

}
