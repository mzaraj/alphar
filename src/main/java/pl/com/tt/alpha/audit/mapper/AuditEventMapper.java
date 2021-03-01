package pl.com.tt.alpha.audit.mapper;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import pl.com.tt.alpha.audit.domain.PersistentAuditEventEntity;

import java.util.*;

@Component
public class AuditEventMapper {

	/**
	 * Convert a list of PersistentAuditEventEntity to a list of AuditEvent
	 *
	 * @param persistentAuditEvents the list to convert
	 * @return the converted list.
	 */
	public List<AuditEvent> convertToAuditEvent(Iterable<PersistentAuditEventEntity> persistentAuditEvents) {
		if (persistentAuditEvents == null) {
			return Collections.emptyList();
		}
		List<AuditEvent> auditEvents = new ArrayList<>();
		for (PersistentAuditEventEntity persistentAuditEvent : persistentAuditEvents) {
			auditEvents.add(convertToAuditEvent(persistentAuditEvent));
		}
		return auditEvents;
	}

	/**
	 * Convert a PersistentAuditEventEntity to an AuditEvent
	 *
	 * @param persistentAuditEvent the event to convert
	 * @return the converted list.
	 */
	public AuditEvent convertToAuditEvent(PersistentAuditEventEntity persistentAuditEvent) {
		if (persistentAuditEvent == null) {
			return null;
		}
		return new AuditEvent(persistentAuditEvent.getAuditEventDate(), persistentAuditEvent.getPrincipal(),
			persistentAuditEvent.getAuditEventType().toString(), null);
	}

	/**
	 * Internal conversion. This is needed to support the current SpringBoot actuator AuditEventRepository interface
	 *
	 * @param data the data to convert
	 * @return a map of String, Object
	 */
	public Map<String, Object> convertDataToObjects(Map<String, String> data) {
		Map<String, Object> results = new HashMap<>();

		if (data != null) {
			for (Map.Entry<String, String> entry : data.entrySet()) {
				results.put(entry.getKey(), entry.getValue());
			}
		}
		return results;
	}

	/**
	 * Internal conversion. This method will allow to save additional data.
	 * By default, it will save the object as string
	 *
	 * @param data the data to convert
	 * @return a map of String, String
	 */
	public Map<String, String> convertDataToStrings(Map<String, Object> data) {
		Map<String, String> results = new HashMap<>();

		if (data != null) {
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				// Extract the data that will be saved.
				if (entry.getValue() instanceof WebAuthenticationDetails) {
					WebAuthenticationDetails authenticationDetails = (WebAuthenticationDetails) entry.getValue();
					results.put("remoteAddress", authenticationDetails.getRemoteAddress());
					results.put("sessionId", authenticationDetails.getSessionId());
				} else {
					results.put(entry.getKey(), Objects.toString(entry.getValue()));
				}
			}
		}
		return results;
	}
}
