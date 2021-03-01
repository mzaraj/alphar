package pl.com.tt.alpha.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.com.tt.alpha.events.domain.EventEntity;
import pl.com.tt.alpha.events.domain.EventMembersEntity;

import java.util.List;

public interface EventMembersRepository extends JpaRepository<EventMembersEntity, Long> {
    List<EventMembersEntity> findAllById(Long id);

    List<EventMembersEntity> findEventMembersEntitiesByEventId(Long id);


    @Query(value = "SELECT eme.user FROM EventMembersEntity eme WHERE eme.event= :event")
    List<EventMembersEntity> findOnlyEventMembersEntitiesByEventId(@Param("event") EventEntity event);

    List<EventMembersEntity> getEventMembersEntitiesByEventId(Long eventId);

    List<EventMembersEntity> getOneByEvent(Long eventId);


    void deleteByEventId(Long eventId);

    void deleteByEventIdAndUserId(Long eventId, Long userId);
}
