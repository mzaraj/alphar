package pl.com.tt.alpha.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.com.tt.alpha.category.domain.CategoryEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findOneByName(String name);

    Optional<CategoryEntity> findOneByNameIgnoreCase(String name);

    Optional<CategoryEntity> findOneById (Long id);

    List<CategoryEntity> findAll();

    List<CategoryEntity> findAllByOrderByName();


}
