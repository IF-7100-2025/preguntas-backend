package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long>  {

    Optional<QuestionEntity> findById(UUID uuid);

    long countByText(String text);
    
    int countByCategories_Id(Long categoryId);

    @Query("SELECT q FROM QuestionEntity q JOIN q.categories c WHERE c.name IN :categoryNames AND q.isVisible = true")
    List<QuestionEntity> findByCategoryNames(@Param("categoryNames") List<String> categoryNames);

    List<QuestionEntity> findByCreatedBy_Id(String userId);

    @Query("SELECT q FROM QuestionEntity q JOIN q.quizzes quiz WHERE quiz.id = :quizId AND q.id = :questionId")
    Optional<QuestionEntity> findByQuizIdAndId(@Param("quizId") UUID quizId, @Param("questionId") UUID questionId);

    List<QuestionEntity> findByIsVisibleTrue();
    
}
