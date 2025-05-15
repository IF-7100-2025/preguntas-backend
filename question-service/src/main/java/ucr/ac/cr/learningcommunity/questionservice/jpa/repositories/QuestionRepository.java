package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long>  {

    //long countByCategory_Id(Long id);
    long countByText(String text);


}
