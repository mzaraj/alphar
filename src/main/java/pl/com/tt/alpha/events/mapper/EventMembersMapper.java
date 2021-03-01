package pl.com.tt.alpha.events.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.com.tt.alpha.common.Mapper;
import pl.com.tt.alpha.events.domain.EventMembersEntity;
import pl.com.tt.alpha.events.vm.EventMembersVm;

@Component
@RequiredArgsConstructor
public class EventMembersMapper implements Mapper<EventMembersEntity, EventMembersVm> {

    private final ModelMapper modelMapper;


    @Override
    public EventMembersEntity toEntity(EventMembersVm VM) {
        return modelMapper.map(VM, EventMembersEntity.class);
    }

    @Override
    public EventMembersVm toVm(EventMembersEntity entity) {
        return modelMapper.map(entity, EventMembersVm.class);
    }
}
