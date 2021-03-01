package pl.com.tt.alpha.user.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.NotAudited;
import pl.com.tt.alpha.common.AbstractAuditingEntity;
import pl.com.tt.alpha.common.IEntity;
import pl.com.tt.alpha.configuration.Constants;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserEntity extends AbstractAuditingEntity implements IEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Pattern(regexp = Constants.LOGIN_REGEX)
	@Size(min = 1, max = 50)
	@Column(length = 50, unique = true, nullable = false)
	private String login;

	@NotNull
	@Size(min = 60, max = 60)
	@Column(name = "password_hash", length = 60, nullable = false)
	private String password;

	@Size(max = 50)
	@Column(name = "first_name", length = 50)
	private String firstName;

	@Size(max = 50)
	@Column(name = "last_name", length = 50)
	private String lastName;

    @Size(min = 2, max = 50)
    @Column(name = "city", length = 25)
    private String city;

    @Column(name = "bday")
    private Instant bday;

	@Email
	@Size(min = 5, max = 254)
	@Column(length = 254, unique = true)
	private String email;

	@NotNull
	@Column(nullable = false)
	private boolean active;

	@Column(name = "lang_key")
	private String langKey;

	@Size(max = 20)
	@Column(name = "activation_key", length = 20)
	private String activationKey;

	@Size(max = 20)
	@Column(name = "reset_key", length = 20)
	private String resetKey;

	@Column(name = "reset_date")
	private Instant resetDate = null;


	@Basic(fetch =FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private String avatar;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToMany
	@NotAudited
	@JoinTable(name = "user_authorities", joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {
		@JoinColumn(name = "authority_name", referencedColumnName = "name")})
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@BatchSize(size = 20)
	private Set<AuthorityEntity> authorities = new HashSet<>();
}
