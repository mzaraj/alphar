package pl.com.tt.alpha.audit.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.com.tt.alpha.audit.domain.PersistentAuditEventEntity;
import pl.com.tt.alpha.audit.vm.AuditVm;
import pl.com.tt.alpha.common.Mapper;

@Component
@RequiredArgsConstructor
public class AuditMapper implements Mapper<PersistentAuditEventEntity, AuditVm> {

	private final ModelMapper modelMapper;

	@Override
	public PersistentAuditEventEntity toEntity(AuditVm vm) {
		return modelMapper.map(vm, PersistentAuditEventEntity.class);
	}

	@Override
	public AuditVm toVm(PersistentAuditEventEntity entity) {
		return modelMapper.map(entity, AuditVm.class);

	}
}
