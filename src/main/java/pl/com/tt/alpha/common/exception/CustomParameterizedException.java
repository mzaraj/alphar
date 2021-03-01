package pl.com.tt.alpha.common.exception;

import pl.com.tt.alpha.common.error.vm.ParameterizedErrorVM;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;


public class CustomParameterizedException extends AbstractMsgKeyException {

	private static final String PARAM = "param";

	private final Map<String, String> paramMap = new HashMap<>();

	public CustomParameterizedException(String key, String message, String... params) {
		super(key, message);
		if (nonNull(params) && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				paramMap.put(PARAM + i, params[i]);
			}
		}
	}

	public CustomParameterizedException(String key, String message, Map<String, String> paramMap) {
		super(key, message);
		this.paramMap.putAll(paramMap);
	}

	CustomParameterizedException(String key, String message) {
		super(key, message);
	}

	public ParameterizedErrorVM getErrorVM() {
		return new ParameterizedErrorVM(this.getMessage(), paramMap);
	}
}
