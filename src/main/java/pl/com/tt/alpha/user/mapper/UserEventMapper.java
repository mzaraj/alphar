package pl.com.tt.alpha.user.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.com.tt.alpha.common.Mapper;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.vm.UserEventVm;

@Component
@RequiredArgsConstructor
public class UserEventMapper implements Mapper<UserEntity, UserEventVm>{

        private final ModelMapper modelMapper;

        @Override
        public UserEntity toEntity(UserEventVm VM) {
            return modelMapper.map(VM, UserEntity.class);
        }

        @Override
        public UserEventVm toVm(UserEntity entity) {
            return modelMapper.map(entity, UserEventVm.class);
        }
    }

