package pl.com.tt.alpha.events.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.com.tt.alpha.common.Mapper;
import pl.com.tt.alpha.events.domain.EventEntity;
import pl.com.tt.alpha.events.vm.EventVm;

@Component
@RequiredArgsConstructor
public class EventMapper implements Mapper<EventEntity, EventVm> {

    private final ModelMapper modelMapper;

    @Override
    public EventEntity toEntity(EventVm VM) {
        return modelMapper.map(VM, EventEntity.class);
    }

    @Override
    public EventVm toVm(EventEntity entity) {
        return modelMapper.map(entity, EventVm.class);
    }
}
