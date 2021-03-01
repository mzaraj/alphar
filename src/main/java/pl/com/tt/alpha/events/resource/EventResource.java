package pl.com.tt.alpha.events.resource;

import io.github.jhipster.web.util.ResponseUtil;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.com.tt.alpha.common.helper.HeaderHelper;
import pl.com.tt.alpha.common.helper.PaginationHelper;
import pl.com.tt.alpha.events.domain.EventEntity;
import pl.com.tt.alpha.events.service.EventService;
import pl.com.tt.alpha.events.vm.EventFilteredVm;
import pl.com.tt.alpha.events.vm.EventVm;
import pl.com.tt.alpha.events.vm.EventVmToUpdate;
import pl.com.tt.alpha.security.AuthoritiesConstants;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/event")
@AllArgsConstructor
public class EventResource {

    private EventService eventService;

    @PostMapping
    @Secured({AuthoritiesConstants.USER,AuthoritiesConstants.ADMIN})
    public ResponseEntity<EventVm> createEvent( @RequestBody EventVm eventVm) throws URISyntaxException {
        log.debug("REST request to save EventEntity", eventVm);
        EventVm newEvent = eventService.createEvent(eventVm);
        return  ResponseEntity.created(new URI("/event/" + newEvent.getName()))
            .headers(HeaderHelper.createAlert("event created", newEvent.getName())).body(newEvent);

    }

    @PutMapping("/{id}")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<EventVm> updateEvent(@PathVariable("id") Long id, @Valid @RequestBody EventVmToUpdate eventVm){
        log.debug("REST request to save EventEntity", eventVm);

        Optional<EventVm> updateEvent = eventService.updateEvent(id,eventVm);

        return ResponseUtil.wrapOrNotFound(updateEvent,HeaderHelper.createAlert("event.updated", updateEvent.get().getName()));
    }


    @DeleteMapping("/{id}")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id) {
        eventService.deleteEventById(id);
        return ResponseEntity.ok().headers(HeaderHelper.createAlert("event.deleted", id.toString())).build();
    }

    @GetMapping("/all")
    @Secured({AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN})
    public ResponseEntity<List<EventEntity>> getAllEvents(){
        final List<EventEntity>  eventVmList =  eventService.getAllEvent();
        return new ResponseEntity<>( eventVmList, HttpStatus.OK);
    }


        @GetMapping(value = "/filtered")
    public ResponseEntity<List<EventEntity>> getFilteredEvents(@ApiParam EventFilteredVm eventFilteredVm, Pageable pageable){
        final Page<EventEntity> page = eventService.getFilteredEvent(eventFilteredVm, pageable);
        HttpHeaders headers = PaginationHelper.generatePaginationHttpHeaders(page, "/api/event/filtered");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    @Secured({AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN})
    public ResponseEntity<EventVm> getEvent(@PathVariable Long id){
        log.debug("REST request to get event:", id);
        return ResponseUtil.wrapOrNotFound( eventService.getEventById(id));
    }

    @PostMapping("/join")
    @Secured({AuthoritiesConstants.USER,AuthoritiesConstants.ADMIN})
    public ResponseEntity<EventVm> joinToEvent( @RequestBody EventVm eventVm) throws URISyntaxException {
        log.debug("REST request to save EventEntity", eventVm);
        EventVm newEvent = eventService.createEvent(eventVm);
        return  ResponseEntity.created(new URI("/event/" + newEvent.getName()))
            .headers(HeaderHelper.createAlert("event created", newEvent.getName())).body(newEvent);

    }









}
