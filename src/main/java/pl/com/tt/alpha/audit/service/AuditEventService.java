package pl.com.tt.alpha.audit.service;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.com.tt.alpha.audit.domain.PersistentAuditEventEntity;
import pl.com.tt.alpha.audit.vm.AuditVm;
import pl.com.tt.alpha.common.IAudit;

import java.time.Instant;
import java.util.Optional;

public interface AuditEventService {

	Page<AuditEvent> findAll(Pageable pageable);

	Page<AuditEvent> findByDates(Instant fromDate, Instant toDate, Pageable pageable);

	Optional<AuditEvent> find(Long id);

	Page<AuditVm> findAllByEntityNameAndEntityId(String entity, Long entityId, Pageable pageable);

	PersistentAuditEventEntity saveAudit(String principal, Long idEntity, IAudit audit);



}
