package pl.com.tt.alpha.user.mapper;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.com.tt.alpha.common.Mapper;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.vm.UserMemberOnEventVm;

@Component
@RequiredArgsConstructor
public class UserMemberOnEventVmMapper implements Mapper<UserEntity, UserMemberOnEventVm> {

    private ModelMapper modelMapper;

    @Override
    public UserEntity toEntity(UserMemberOnEventVm VM) {
        return modelMapper.map(VM, UserEntity.class);
    }

    @Override
    public UserMemberOnEventVm toVm(UserEntity entity) {
        return modelMapper.map(entity,UserMemberOnEventVm.class);
    }
}
