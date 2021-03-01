package pl.com.tt.alpha.audit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.com.tt.alpha.audit.domain.PersistentAuditEventEntity;

import java.time.Instant;
import java.util.List;

@Repository
public interface PersistenceAuditEventRepository extends JpaRepository<PersistentAuditEventEntity, Long> {


	List<PersistentAuditEventEntity> findByPrincipalAndAuditEventDateAfterAndAuditEventType(String principle, Instant after, String type);

	Page<PersistentAuditEventEntity> findAllByAuditEventDateBetween(Instant fromDate, Instant toDate, Pageable pageable);

	@Query("FROM PersistentAuditEventEntity pae WHERE pae.auditEntityName = :entity AND pae.entityId = :entityId")
	Page<PersistentAuditEventEntity> findAllByEntityAndEntityId(@Param("entity") String entity, @Param("entityId") Long entityId, Pageable pageable);
}
