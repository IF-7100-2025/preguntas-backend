package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long>  {
        int countByCategories_Id(Long categoryId);
}
