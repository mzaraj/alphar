package pl.com.tt.alpha.events.service;

import pl.com.tt.alpha.events.domain.EventMembersEntity;
import pl.com.tt.alpha.events.vm.EventMembersVm;

import java.util.List;

public interface EventMembersService {

    EventMembersVm addToEvent(EventMembersVm eventMembersVm);

    List<EventMembersEntity> getAllMembers(Long eventId);

    void deleteMemberFromEvent(Long eventId, Long userId);

    void deleteAllMembersFromEventMemberTable(Long eventId);

    int getCountElements();
}
