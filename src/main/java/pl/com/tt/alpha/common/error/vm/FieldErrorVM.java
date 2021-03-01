package pl.com.tt.alpha.common.error.vm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FieldErrorVM {

	private final String objectName;
	private final String field;
	private final String message;

}
