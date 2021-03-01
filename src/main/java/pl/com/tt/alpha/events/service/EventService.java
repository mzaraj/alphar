package pl.com.tt.alpha.events.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.com.tt.alpha.events.domain.EventEntity;
import pl.com.tt.alpha.events.vm.EventFilteredVm;
import pl.com.tt.alpha.events.vm.EventVm;
import pl.com.tt.alpha.events.vm.EventVmToUpdate;

import java.util.List;
import java.util.Optional;

public interface EventService {
    EventVm createEvent(EventVm eventVm);

    Optional<EventVm> updateEvent(Long id, EventVmToUpdate eventVm);

    void deleteEventById(Long id);

    List<EventEntity> getAllEvent();

    Optional<EventVm> getEventById(Long id);

    Page<EventEntity> getFilteredEvent( EventFilteredVm eventFilteredVm, Pageable pageable);
}
