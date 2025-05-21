package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.CategoryEntity;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findAllByNameIn(List<String> names);
    boolean existsByName(String name);
    List<CategoryEntity> findByNameContainingIgnoreCase(String name);
}
