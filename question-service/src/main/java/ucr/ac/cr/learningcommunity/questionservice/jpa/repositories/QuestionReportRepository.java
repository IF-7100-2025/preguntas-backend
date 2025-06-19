package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionReportEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionReportRepository extends JpaRepository<QuestionReportEntity, UUID> {

    List<QuestionReportEntity> findByQuestion(QuestionEntity question);
    List<QuestionReportEntity> findByQuestionAndStatus(QuestionEntity question, String status);

    long countByQuestion(QuestionEntity question);

    @Query("SELECT DISTINCT r.question FROM QuestionReportEntity r")
    List<QuestionEntity> findDistinctQuestionWithReports();

    @Query("SELECT DISTINCT r.question FROM QuestionReportEntity r WHERE r.status = 'PENDING' AND r.question.isVisible = true")
    List<QuestionEntity> findDistinctQuestionWithPendingReports();

}
