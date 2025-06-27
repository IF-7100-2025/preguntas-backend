package ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.DeleteQuestionHandler;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionReportEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionReportRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeleteQuestionHandlerImpl implements DeleteQuestionHandler {

    private final QuestionRepository questionRepository;
    private final QuestionReportRepository questionReportRepository;

    public DeleteQuestionHandlerImpl(QuestionRepository questionRepository,
                                     QuestionReportRepository questionReportRepository) {
        this.questionRepository = questionRepository;
        this.questionReportRepository = questionReportRepository;
    }

    @Override
    @Transactional
    public Result deleteQuestion(String questionId) {
        if (questionId == null || questionId.isBlank()) {
            return new Result.NotFound(404, "questionId is required");
        }
        UUID qid;
        try {
            qid = UUID.fromString(questionId);
        } catch (IllegalArgumentException ex) {
            return new Result.NotFound(404, "Invalid questionId format: " + questionId);
        }

        try {
            Optional<QuestionEntity> optQuestion = questionRepository.findById(qid);
            if (optQuestion.isEmpty()) {
                return new Result.NotFound(404, "Question not found: " + questionId);
            }
            QuestionEntity question = optQuestion.get();

            //Borrado lógico de la pregunta
            question.setIsVisible(false);
            questionRepository.save(question);

            //Actualizar SOLO los reportes PENDING a REMOVED
            List<QuestionReportEntity> pendingReports =
                    questionReportRepository.findByQuestionAndStatus(question, "PENDING");
            if (!pendingReports.isEmpty()) {
                for (QuestionReportEntity r : pendingReports) {
                    r.setStatus("REMOVED");
                }
                questionReportRepository.saveAll(pendingReports);
            }

            return new Result.Success(200, "Question deleted (logical) and pending reports marked REMOVED");
        } catch (Exception ex) {
            return new Result.InternalError(500, "Internal error deleting question: " + ex.getMessage());
        }
    }
}
