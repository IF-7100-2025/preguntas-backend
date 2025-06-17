package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.AnswerOptionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnswerOptionRepository extends JpaRepository<AnswerOptionEntity, Long> {
    Optional<AnswerOptionEntity> findById(Long id);

    List<AnswerOptionEntity> findByQuestionId(UUID questionId);
}
