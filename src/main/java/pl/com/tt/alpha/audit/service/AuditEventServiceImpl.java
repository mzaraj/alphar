package pl.com.tt.alpha.audit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.tt.alpha.audit.domain.PersistentAuditEventEntity;
import pl.com.tt.alpha.audit.mapper.AuditEventMapper;
import pl.com.tt.alpha.audit.mapper.AuditMapper;
import pl.com.tt.alpha.audit.repository.PersistenceAuditEventRepository;
import pl.com.tt.alpha.audit.vm.AuditVm;
import pl.com.tt.alpha.common.IAudit;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuditEventServiceImpl implements AuditEventService {

	private final PersistenceAuditEventRepository persistenceAuditEventRepository;
	private final AuditEventMapper auditEventConverter;
	private final AuditMapper auditMapper;


	@Override
	public Page<AuditEvent> findAll(Pageable pageable) {
		return persistenceAuditEventRepository.findAll(pageable).map(auditEventConverter::convertToAuditEvent);
	}

	@Override
	public Page<AuditEvent> findByDates(Instant fromDate, Instant toDate, Pageable pageable) {
		return persistenceAuditEventRepository.findAllByAuditEventDateBetween(fromDate, toDate, pageable).map(auditEventConverter::convertToAuditEvent);
	}

	@Override
	public Optional<AuditEvent> find(Long id) {
		return Optional.ofNullable(persistenceAuditEventRepository.findById(id)).filter(Optional::isPresent).map(Optional::get)
					   .map(auditEventConverter::convertToAuditEvent);
	}

	@Override
	public Page<AuditVm> findAllByEntityNameAndEntityId(String entity, Long userId, Pageable pageable) {
		return persistenceAuditEventRepository.findAllByEntityAndEntityId(entity, userId, pageable).map(auditMapper::toVm);
	}

	@Override
	public PersistentAuditEventEntity saveAudit(String principal, Long idEntity, IAudit audit) {
		return setAuditEntity(principal, idEntity, audit);
	}

	private PersistentAuditEventEntity setAuditEntity(String principal, Long idEntity, IAudit audit) {
		PersistentAuditEventEntity persistentAuditEventEntity = new PersistentAuditEventEntity();
		persistentAuditEventEntity.setPrincipal(principal);
		persistentAuditEventEntity.setAuditEventDate(Instant.now());
		persistentAuditEventEntity.setAuditEntityName(audit.getType());
		persistentAuditEventEntity.setAuditEventType(audit.toString());
		persistentAuditEventEntity.setEntityId(idEntity);

		return persistenceAuditEventRepository.save(persistentAuditEventEntity);
	}
}
