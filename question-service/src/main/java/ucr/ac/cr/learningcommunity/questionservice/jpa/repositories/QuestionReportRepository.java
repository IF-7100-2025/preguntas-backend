package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionReportEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;

import java.time.LocalDate;
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


    boolean existsByUserAndQuestionId(UserEntity user, UUID questionId);
    // metodo para contar reportes diarios de un usuario
    @Query("SELECT COUNT(r) FROM QuestionReportEntity r WHERE r.user = :user AND DATE(r.reportedAt) = :date")
    long countByUserAndReportDate(UserEntity user, LocalDate date);
}
