package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.AnswerOptionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnswerOptionRepository extends JpaRepository<AnswerOptionEntity, Long> {
    Optional<AnswerOptionEntity> findById(Long id);

    List<AnswerOptionEntity> findByQuestionId(UUID questionId);

    @Query("SELECT a.id FROM AnswerOptionEntity a WHERE a.question.id = :questionId AND a.isCorrect = true")
    List<Long> findCorrectAnswersByQuestionId(@Param("questionId") UUID questionId);
}
