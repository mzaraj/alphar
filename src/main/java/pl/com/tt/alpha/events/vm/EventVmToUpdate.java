package pl.com.tt.alpha.events.vm;

import lombok.Data;
import pl.com.tt.alpha.common.ViewModel;

import java.time.Instant;

@Data
public class EventVmToUpdate implements ViewModel {
    private Long id;

    private String name;

    private String city;

    private String placeId;

    private String description;

    private Instant date;

    private Long category;

}
