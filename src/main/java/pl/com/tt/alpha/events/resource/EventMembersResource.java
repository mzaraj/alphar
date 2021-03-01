package pl.com.tt.alpha.events.resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.com.tt.alpha.common.helper.HeaderHelper;
import pl.com.tt.alpha.events.domain.EventMembersEntity;
import pl.com.tt.alpha.events.service.EventMembersService;
import pl.com.tt.alpha.events.vm.EventMembersVm;
import pl.com.tt.alpha.security.AuthoritiesConstants;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/event/members")
@AllArgsConstructor
public class EventMembersResource {

    private EventMembersService eventMembersService;

    @PostMapping
    @Secured({AuthoritiesConstants.USER,AuthoritiesConstants.ADMIN})
    public ResponseEntity<EventMembersVm> addToEvent( @RequestBody EventMembersVm eventMembersVm) throws URISyntaxException {
        log.debug("REST request to save EventEventMembersEntityMembers", eventMembersVm);
        EventMembersVm newEventMembers = eventMembersService.addToEvent(eventMembersVm);
        return  ResponseEntity.created(new URI("/event/" + newEventMembers.getId()))
            .headers(HeaderHelper.createAlert("add members to event", newEventMembers.getId().toString())).body(newEventMembers);
    }

    @GetMapping("/get/{eventId}")
    public ResponseEntity<List<EventMembersEntity>> getAllMembers(@PathVariable Long eventId){
        final List<EventMembersEntity> eventVmList =  eventMembersService.getAllMembers(eventId);
        return new ResponseEntity<>( eventVmList, HttpStatus.OK);
    }

    @DeleteMapping("/delete/eventId/{eventId}/userId/{userId}")
    public ResponseEntity<Void> deleteFromEvent (@PathVariable Long eventId,@PathVariable Long userId) throws URISyntaxException {
        eventMembersService.deleteMemberFromEvent(eventId,userId);
        return ResponseEntity.ok().headers(HeaderHelper.createAlert("delete user with id ", userId.toString())).build();
    }

    @GetMapping("/get/elements")
    public int getCountElements(){
        final int countList =  eventMembersService.getCountElements();
        return countList;
    }



}
