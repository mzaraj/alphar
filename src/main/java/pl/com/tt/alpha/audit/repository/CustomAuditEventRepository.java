package pl.com.tt.alpha.audit.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.com.tt.alpha.audit.domain.PersistentAuditEventEntity;
import pl.com.tt.alpha.audit.mapper.AuditEventMapper;
import pl.com.tt.alpha.configuration.Constants;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomAuditEventRepository implements AuditEventRepository {

	private static final String AUTHORIZATION_FAILURE = "AUTHORIZATION_FAILURE";

	protected static final int EVENT_DATA_COLUMN_MAX_LENGTH = 255;

	private final PersistenceAuditEventRepository persistenceAuditEventRepository;
	private final AuditEventMapper auditEventConverter;

	@Override
	public List<AuditEvent> find(String principal, Instant after, String type) {
		Iterable<PersistentAuditEventEntity> persistentAuditEvents = persistenceAuditEventRepository
			.findByPrincipalAndAuditEventDateAfterAndAuditEventType(principal, after, type);
		return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void add(AuditEvent event) {
		if (!AUTHORIZATION_FAILURE.equals(event.getType()) && !Constants.ANONYMOUS_USER.equals(event.getPrincipal())) {

			PersistentAuditEventEntity persistentAuditEvent = new PersistentAuditEventEntity();
			persistentAuditEvent.setPrincipal(event.getPrincipal());
			persistentAuditEvent.setAuditEventType(event.getType());
			persistentAuditEvent.setAuditEventDate(event.getTimestamp());
			persistenceAuditEventRepository.save(persistentAuditEvent);
		}
	}

	/**
	 * Truncate event data that might exceed column length.
	 */
	private Map<String, String> truncate(Map<String, String> data) {
		Map<String, String> results = new HashMap<>();

		if (data != null) {
			for (Map.Entry<String, String> entry : data.entrySet()) {
				String value = entry.getValue();
				if (value != null) {
					int length = value.length();
					if (length > EVENT_DATA_COLUMN_MAX_LENGTH) {
						value = value.substring(0, EVENT_DATA_COLUMN_MAX_LENGTH);
						log.warn("Event data for {} too long ({}) has been truncated to {}. Consider increasing column width.", entry.getKey(), length,
							EVENT_DATA_COLUMN_MAX_LENGTH);
					}
				}
				results.put(entry.getKey(), value);
			}
		}
		return results;
	}
}
