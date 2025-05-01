package ucr.ac.cr.learningcommunity.iaservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.learningcommunity.iaservice.jpa.entities.CategoryEmbedding;


public interface CategoryEmbeddingRepository extends JpaRepository<CategoryEmbedding, Long> {
    CategoryEmbedding findByCategoryId(Long categoryId);
}
