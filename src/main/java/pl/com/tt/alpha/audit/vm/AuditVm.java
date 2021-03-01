package pl.com.tt.alpha.audit.vm;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.com.tt.alpha.common.ViewModel;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@NoArgsConstructor
public class AuditVm implements ViewModel {

	private Long id;

	@NotNull
	private String principal;

	@NotNull
	private Instant auditEventDate;

	@NotNull String auditEventType;
}
