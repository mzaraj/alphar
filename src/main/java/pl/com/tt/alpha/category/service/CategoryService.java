package pl.com.tt.alpha.category.service;

import pl.com.tt.alpha.category.domain.CategoryEntity;
import pl.com.tt.alpha.category.vm.request.CategoryVm;

import java.util.List;
import java.util.Optional;


public interface CategoryService {

    CategoryEntity createCategory( CategoryVm categoryVm);

    Optional<CategoryVm> updateCategory(Long id,CategoryVm categoryVm);

    List<CategoryEntity> getAllCategory();

    Optional<CategoryVm> getCategoryById(Long categoryId);

    void deleteCategoryById(Long id);
}
