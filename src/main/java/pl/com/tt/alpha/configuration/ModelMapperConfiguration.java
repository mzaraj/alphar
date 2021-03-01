package pl.com.tt.alpha.configuration;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.com.tt.alpha.events.domain.EventEntity;
import pl.com.tt.alpha.events.domain.EventMembersEntity;
import pl.com.tt.alpha.events.vm.EventMembersVm;
import pl.com.tt.alpha.events.vm.EventVm;
import pl.com.tt.alpha.profile.vm.ProfileVM;
import pl.com.tt.alpha.user.domain.AuthorityEntity;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.vm.UserAuthoritiesVM;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                   .setMatchingStrategy(MatchingStrategies.STRICT);

		Converter<Set<AuthorityEntity>, Set<String>> toSetString =
			source -> source.getSource().stream().map(AuthorityEntity::getName).collect(Collectors.toSet());

        modelMapper.typeMap(UserEntity.class, UserAuthoritiesVM.class)
				   .addMappings(mapper ->
					   mapper.using(toSetString).map(UserEntity::getAuthorities, UserAuthoritiesVM::setAuthorities)
				   );

        modelMapper.typeMap(UserEntity.class, ProfileVM.class)
				   .addMappings(mapper ->
					   mapper.using(toSetString).map(UserEntity::getAuthorities, ProfileVM::setAuthorities)
				   );

        modelMapper.typeMap(EventMembersEntity.class, EventMembersVm.class)
            .addMappings(mapper -> mapper.map(eventEntity -> eventEntity.getEvent().getId(), EventMembersVm::setEventId));

        modelMapper.typeMap(EventMembersEntity.class, EventMembersVm.class)
            .addMappings(mapper -> mapper.map(eventEntity -> eventEntity.getUser().getId(), EventMembersVm::setUserId));


		modelMapper.typeMap(EventEntity.class, EventVm.class)
				   .addMappings(mapper -> mapper.map(eventEntity -> eventEntity.getAuthor().getLogin(), EventVm::setAuthor));

        modelMapper.typeMap(EventEntity.class, EventVm.class)
            .addMappings(mapper -> mapper.map(eventEntity -> eventEntity.getCategory().getId(), EventVm::setCategory));

        return modelMapper;
    }
}
