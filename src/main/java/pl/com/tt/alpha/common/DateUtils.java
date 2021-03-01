package pl.com.tt.alpha.common;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class DateUtils {

	public static Instant createStartData(LocalDate fromDate) {
		return fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
	}

	public static Instant createEndData(LocalDate toDate) {
		return toDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant();
	}
}
