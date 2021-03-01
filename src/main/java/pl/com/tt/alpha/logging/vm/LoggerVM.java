package pl.com.tt.alpha.logging.vm;

import ch.qos.logback.classic.Logger;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class LoggerVM {

    private String name;
    private String level;

	public LoggerVM(Logger logger) {
		this.name = logger.getName();
		this.level = logger.getEffectiveLevel().toString();
	}

}
