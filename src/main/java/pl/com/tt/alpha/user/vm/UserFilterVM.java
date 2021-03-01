package pl.com.tt.alpha.user.vm;

import lombok.Data;
import pl.com.tt.alpha.filters.ConditionType;
import pl.com.tt.alpha.filters.FilterBy;
import pl.com.tt.alpha.filters.Filterable;

@Data
public class UserFilterVM implements Filterable {

	@FilterBy(fieldName = "id", condition = ConditionType.LIKE)
	private Long userId;

	@FilterBy(fieldName = "login", condition = ConditionType.LIKE_IGNORE_CASE)
	private String login;

	@FilterBy(fieldName = "firstName", condition = ConditionType.LIKE_IGNORE_CASE)
	private String firstName;

	@FilterBy(fieldName = "lastName", condition = ConditionType.LIKE_IGNORE_CASE)
	private String lastName;

	@FilterBy(fieldName = "email", condition = ConditionType.LIKE_IGNORE_CASE)
	private String email;
}
