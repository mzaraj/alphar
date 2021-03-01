package pl.com.tt.alpha.category.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.com.tt.alpha.category.domain.CategoryEntity;
import pl.com.tt.alpha.category.vm.request.CategoryVm;
import pl.com.tt.alpha.common.Mapper;

@Component
@RequiredArgsConstructor
public class CategoryMapper implements Mapper<CategoryEntity, CategoryVm> {

    private final ModelMapper modelMapper;


    @Override
    public CategoryEntity toEntity(CategoryVm VM) {
        return modelMapper.map(VM, CategoryEntity.class);
    }

    @Override
    public CategoryVm toVm(CategoryEntity entity) {
        return modelMapper.map(entity, CategoryVm.class);
    }
}
