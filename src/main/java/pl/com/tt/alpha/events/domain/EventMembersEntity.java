package pl.com.tt.alpha.events.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import pl.com.tt.alpha.common.IEntity;
import pl.com.tt.alpha.user.domain.UserEntity;

import javax.persistence.*;

@Data
@Entity
@Table(name = "event_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EventMembersEntity implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventEntity event;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
