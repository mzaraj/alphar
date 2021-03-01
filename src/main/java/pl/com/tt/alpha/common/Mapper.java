package pl.com.tt.alpha.common;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mariusz Batyra on 05.07.2018.
 */
public interface Mapper<ENTITY extends IEntity, VM extends ViewModel> {

	ENTITY toEntity(VM VM);

	VM toVm(ENTITY entity);

	default List<ENTITY> toEntities(List<VM> VMS) {
		return VMS.stream().map(this::toEntity).collect(Collectors.toList());
	}

	default List<VM> toVms(List<ENTITY> entities) {
		return entities.stream().map(this::toVm).collect(Collectors.toList());
	}

}
