package pl.com.tt.alpha.category.resource;

import io.github.jhipster.web.util.ResponseUtil;
import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.com.tt.alpha.category.domain.CategoryEntity;
import pl.com.tt.alpha.category.repository.CategoryRepository;
import pl.com.tt.alpha.category.service.CategoryService;
import pl.com.tt.alpha.category.vm.request.CategoryVm;
import pl.com.tt.alpha.common.exception.ObjectAlreadyExistException;
import pl.com.tt.alpha.common.helper.HeaderHelper;
import pl.com.tt.alpha.security.AuthoritiesConstants;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/category")
@AllArgsConstructor
public class CategoryResources {


    private CategoryRepository categoryRepository;
    private CategoryService categoryService;


    @PostMapping
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<CategoryEntity> createCategory(@Valid @RequestBody CategoryVm categoryVm) throws URISyntaxException{
        log.debug("REST request to save CaregoryEntity", categoryVm);
        if(categoryRepository.findOneByName(categoryVm.getName()).isPresent()){
            throw new ObjectAlreadyExistException("Taka kategoria już istnieje");
        }
        else {
            CategoryEntity newCategory = categoryService.createCategory(categoryVm);
            return ResponseEntity.created(new URI("/category/" + newCategory.getName()))
                .headers(HeaderHelper.createAlert("category created", newCategory.getName())).body(newCategory);
        }
    }


    @PutMapping("/{id}")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<CategoryVm> updateCategory(@PathVariable("id") Long id, @Valid @RequestBody CategoryVm categoryVm) {
        log.debug("updated category" + categoryVm.getId());

        Optional<CategoryVm> updateCategory = categoryService.updateCategory(id,categoryVm);

        return ResponseUtil.wrapOrNotFound(updateCategory, HeaderHelper.createAlert("userManagement.updated", updateCategory.get().getName()));
}


    @DeleteMapping("/{id}")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok().headers(HeaderHelper.createAlert("category.deleted", id.toString())).build();
    }

    @GetMapping("/all")
    @Secured({AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN})
    public ResponseEntity<List<CategoryEntity>> getAllCategories(){
        final List<CategoryEntity> categoryList = categoryService.getAllCategory();
    return new ResponseEntity<>(categoryList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<CategoryVm> getCategory(@PathVariable Long id) {
        log.debug("REST request to get Category Name : {}", id);
        return ResponseUtil.wrapOrNotFound(categoryService.getCategoryById(id));
    }


}


