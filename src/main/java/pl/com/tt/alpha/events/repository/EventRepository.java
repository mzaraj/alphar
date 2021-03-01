package pl.com.tt.alpha.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pl.com.tt.alpha.events.domain.EventEntity;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {
    Optional<EventEntity> findOneById(Long eventId);

    EventEntity getOneById(Long eventId);
}
