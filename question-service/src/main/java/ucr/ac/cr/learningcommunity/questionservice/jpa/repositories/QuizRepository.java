package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuizEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<QuizEntity, UUID> {
    List<QuizEntity> findAll();
    Optional<QuizEntity> findById(UUID id);

    @Query("SELECT COUNT(q) " +
            "FROM QuestionEntity q " +
                "JOIN q.quizzes quiz " +
            "WHERE quiz.id = :quizId")
    int countQuestionsByQuizId(UUID quizId);

    @Modifying
    @Query("UPDATE QuizEntity q " +
            "SET q.status = :state, q.completed = :completed " +
            "WHERE q.id = :quizId")
    void updateQuizStatusAndCompletion(@Param("quizId") UUID quizId,
                                       @Param("state") String state,
                                       @Param("completed")LocalDateTime completed);
}
