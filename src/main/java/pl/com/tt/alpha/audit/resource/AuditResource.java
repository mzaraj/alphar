package pl.com.tt.alpha.audit.resource;

import io.github.jhipster.web.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.tt.alpha.audit.service.AuditEventService;
import pl.com.tt.alpha.audit.vm.AuditVm;
import pl.com.tt.alpha.common.DateUtils;
import pl.com.tt.alpha.common.helper.PaginationHelper;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/management/audits")
@RequiredArgsConstructor
public class AuditResource {

	private final AuditEventService auditEventService;

	@GetMapping
	public ResponseEntity<List<AuditEvent>> getAll(Pageable pageable) {
		Page<AuditEvent> page = auditEventService.findAll(pageable);
		HttpHeaders headers = PaginationHelper.generatePaginationHttpHeaders(page, "/management/audits");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	@GetMapping(params = {"fromDate", "toDate"})
	public ResponseEntity<List<AuditEvent>> getByDates(@RequestParam(value = "fromDate") LocalDate fromDate, @RequestParam(value = "toDate") LocalDate toDate,
		Pageable pageable) {

		Page<AuditEvent> page = auditEventService.findByDates(DateUtils.createStartData(fromDate), DateUtils.createEndData(toDate), pageable);

		HttpHeaders headers = PaginationHelper.generatePaginationHttpHeaders(page, "/management/audits");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	@GetMapping("/entity/{entity}/entityId/{entityId}")
	public ResponseEntity<List<AuditVm>> getAllByEntityAndEntityId(@PathVariable String entity, @PathVariable Long entityId, Pageable pageable) {
		Page<AuditVm> page = auditEventService.findAllByEntityNameAndEntityId(entity, entityId, pageable);
		HttpHeaders headers = PaginationHelper.generatePaginationHttpHeaders(page, "/management/audits/entity/{entity}/entityId/{entityId}");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	@GetMapping("/{id:.+}")
	public ResponseEntity<AuditEvent> get(@PathVariable Long id) {
		return ResponseUtil.wrapOrNotFound(auditEventService.find(id));
	}
}
