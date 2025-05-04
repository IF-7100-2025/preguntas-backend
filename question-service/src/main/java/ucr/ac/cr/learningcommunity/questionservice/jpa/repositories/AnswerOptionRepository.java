package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.AnswerOptionEntity;

public interface AnswerOptionRepository extends JpaRepository<AnswerOptionEntity, Long> {
}
