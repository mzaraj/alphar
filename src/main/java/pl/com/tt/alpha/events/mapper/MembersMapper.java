package pl.com.tt.alpha.events.mapper;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.com.tt.alpha.common.Mapper;
import pl.com.tt.alpha.events.domain.EventMembersEntity;
import pl.com.tt.alpha.user.vm.UserEventVm;

@Component
@RequiredArgsConstructor
public class MembersMapper implements Mapper<EventMembersEntity, UserEventVm> {

    private final ModelMapper modelMapper;


    @Override
    public EventMembersEntity toEntity(UserEventVm VM) {
        return modelMapper.map(VM, EventMembersEntity.class);
    }

    @Override
    public UserEventVm toVm(EventMembersEntity entity) {
        return modelMapper.map(entity, UserEventVm.class);
    }
}
