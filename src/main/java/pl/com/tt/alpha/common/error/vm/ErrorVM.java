package pl.com.tt.alpha.common.error.vm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorVM {

	private final String message;
	private final String description;
	private List<FieldErrorVM> fieldErrors;

	public ErrorVM(String message) {
		this(message, null);
	}

	public void add(String objectName, String field, String message) {
		if (isNull(fieldErrors)) {
			fieldErrors = new ArrayList<>();
		}
		fieldErrors.add(new FieldErrorVM(objectName, field, message));
	}

}
