package pl.com.tt.alpha.profile.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.com.tt.alpha.common.Mapper;
import pl.com.tt.alpha.profile.vm.ProfileVM;
import pl.com.tt.alpha.user.domain.UserEntity;

@Component
@RequiredArgsConstructor
public class ProfileMapper implements Mapper<UserEntity, ProfileVM> {

    private final ModelMapper modelMapper;

    @Override
    public UserEntity toEntity(ProfileVM VM) {
        return modelMapper.map(VM, UserEntity.class);
    }

    @Override
    public ProfileVM toVm(UserEntity entity) {
        return modelMapper.map(entity, ProfileVM.class);
    }
}
