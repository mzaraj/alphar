package pl.com.tt.alpha.common.error.vm;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ParameterizedErrorVM {

	private final String message;
	private final Map<String, String> params;

}
