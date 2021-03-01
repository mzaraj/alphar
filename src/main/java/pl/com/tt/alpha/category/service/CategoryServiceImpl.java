package pl.com.tt.alpha.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.tt.alpha.category.domain.CategoryEntity;
import pl.com.tt.alpha.category.mapper.CategoryMapper;
import pl.com.tt.alpha.category.repository.CategoryRepository;
import pl.com.tt.alpha.category.vm.request.CategoryVm;
import pl.com.tt.alpha.common.exception.ObjectAlreadyExistException;
import pl.com.tt.alpha.common.exception.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper mapper;
    private CategoryRepository categoryRepository;


    @Override
    public CategoryEntity createCategory(CategoryVm categoryVm) {
        CategoryEntity category = mapper.toEntity(categoryVm);
        CategoryEntity save = categoryRepository.save(category);
        log.debug("Created Information for UserEntity: {}", save);
        return save;
    }

    @Override
    public Optional<CategoryVm> updateCategory(Long categoryId, CategoryVm categoryVm) {

        Optional<CategoryEntity> existingCategory = categoryRepository.findOneById(categoryId);
        if (!existingCategory.isPresent())
            throw new ObjectNotFoundException("Category doesn't exist");


        if(categoryRepository.findOneByName(categoryVm.getName()).isPresent())
            throw new ObjectAlreadyExistException("Category already use");


        return Optional.of(categoryRepository.findById(categoryId)).filter(Optional::isPresent).map(Optional::get).map(category -> {
            category.setName(categoryVm.getName());
            log.debug("Changed Information for UserEntity: {}", category);
            return category;
        }).map(mapper::toVm);

    }

    @Override
    public void deleteCategoryById(Long id) {
        if (!categoryRepository.findById(id).isPresent())
            throw new ObjectNotFoundException("Category with id: " + id + " doesn't exist!");

        categoryRepository.deleteById(id);
        log.debug("Delete Category: ", id);
    }

    @Override
    public List<CategoryEntity> getAllCategory() {
       List<CategoryEntity> list = categoryRepository.findAllByOrderByName();
        if(list.isEmpty())
            throw new ObjectNotFoundException("There are no categories");
        return list;
    }

    @Override
    public Optional<CategoryVm> getCategoryById(Long categoryId) {

        Optional<CategoryEntity> categoryEntity = categoryRepository.findOneById(categoryId);
        if(!categoryEntity.isPresent())
            throw new ObjectNotFoundException("Category doesn't exist");
        Optional<CategoryVm> categoryVm = categoryEntity.map(mapper::toVm);

        return categoryVm;
    }


}
