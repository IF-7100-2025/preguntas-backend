package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long>  {

    //long countByCategory_Id(Long id);
    long countByText(String text);

    // obtiene todas las preguntas de las categorías que el usuario indicó
    @Query("SELECT q FROM QuestionEntity q JOIN q.categories c WHERE c.name IN :categoryNames")
    List<QuestionEntity> findByCategoryNames(@Param("categoryNames") List<String> categoryNames);

    // Query para obtener 3 preguntas random de una categoría
    @Query(value = "SELECT * FROM question q " +
            "JOIN question_category qc ON q.id = qc.question_id " +
            "JOIN category c ON qc.category_id = c.id " +
            "WHERE c.name = :categoryName " +
            "ORDER BY RANDOM() " +
            "LIMIT 3", nativeQuery = true)
    List<QuestionEntity> findRandomQuestionsByCategory(@Param("categoryName") String categoryName);
}
