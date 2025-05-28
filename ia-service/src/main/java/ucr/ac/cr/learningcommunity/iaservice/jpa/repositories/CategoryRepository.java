package ucr.ac.cr.learningcommunity.iaservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.learningcommunity.iaservice.jpa.entities.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {}
