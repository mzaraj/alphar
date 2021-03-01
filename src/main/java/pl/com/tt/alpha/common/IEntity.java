package pl.com.tt.alpha.common;

import static java.util.Objects.isNull;

/**
 * Created by Mariusz Batyra on 05.07.2018.
 */
public interface IEntity {

	Long getId();

	default boolean isNew() {
		return isNull(getId());
	}
}
