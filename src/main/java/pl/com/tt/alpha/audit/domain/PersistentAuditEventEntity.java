package pl.com.tt.alpha.audit.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.com.tt.alpha.common.IEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@Table(name = "persistent_audit_event")
public class PersistentAuditEventEntity implements Serializable, IEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id")
	private Long id;

	@NotNull
	@Column(nullable = false)
	private String principal;

	@Column(name = "event_date")
	private Instant auditEventDate;

	@Column(name = "entity_name")
	private String auditEntityName;

	@Column(name = "event_type")
	private String auditEventType;

	@Column(name = "entity_id")
	private Long entityId;

}
