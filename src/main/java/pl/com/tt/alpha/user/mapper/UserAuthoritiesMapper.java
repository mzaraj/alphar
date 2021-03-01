package pl.com.tt.alpha.user.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;
import pl.com.tt.alpha.common.Mapper;
import pl.com.tt.alpha.user.domain.AuthorityEntity;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.repository.AuthorityRepository;
import pl.com.tt.alpha.user.repository.UserRepository;
import pl.com.tt.alpha.user.vm.UserAuthoritiesVM;
import pl.com.tt.alpha.user.vm.UserVM;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserAuthoritiesMapper implements Mapper<UserEntity, UserAuthoritiesVM> {

	private final ModelMapper modelMapper;

	@Override
	public UserEntity toEntity(UserAuthoritiesVM VM) {
		return modelMapper.map(VM, UserEntity.class);
	}

	@Override
	public UserAuthoritiesVM toVm(UserEntity entity) {
		return modelMapper.map(entity, UserAuthoritiesVM.class);
	}
}
