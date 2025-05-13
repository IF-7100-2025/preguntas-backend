package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuizEntity;

import java.util.List;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<QuestionRepository, UUID> {

}
