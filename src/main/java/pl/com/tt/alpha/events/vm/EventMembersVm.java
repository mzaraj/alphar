package pl.com.tt.alpha.events.vm;

import lombok.Data;
import pl.com.tt.alpha.common.ViewModel;

@Data
public class EventMembersVm implements ViewModel {

    private Long id;

    private Long eventId;

    private Long userId;
}
