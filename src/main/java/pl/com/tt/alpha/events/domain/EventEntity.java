package pl.com.tt.alpha.events.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import pl.com.tt.alpha.category.domain.CategoryEntity;
import pl.com.tt.alpha.common.IEntity;
import pl.com.tt.alpha.user.domain.UserEntity;

import javax.persistence.*;
import java.time.Instant;


@Data
@Entity
@Table(name = "events")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EventEntity implements IEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    //location
    private String city;

    private String placeId;
    //end location

    private String description;

    private Instant date;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "author", nullable = false)
    private UserEntity author;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

}
