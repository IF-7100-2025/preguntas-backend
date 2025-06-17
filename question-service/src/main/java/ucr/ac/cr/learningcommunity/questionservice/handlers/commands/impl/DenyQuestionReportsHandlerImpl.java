package ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.DenyQuestionReportsHandler;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionReportEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionReportRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DenyQuestionReportsHandlerImpl implements DenyQuestionReportsHandler {

    private final QuestionRepository questionRepository;
    private final QuestionReportRepository questionReportRepository;

    public DenyQuestionReportsHandlerImpl(QuestionRepository questionRepository,
                                          QuestionReportRepository questionReportRepository) {
        this.questionRepository = questionRepository;
        this.questionReportRepository = questionReportRepository;
    }

    @Override
    @Transactional
    public Result denyReports(String questionId) {
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
            Optional<QuestionEntity> optQ = questionRepository.findById(qid);
            if (optQ.isEmpty()) {
                return new Result.NotFound(404, "Question not found: " + questionId);
            }
            QuestionEntity question = optQ.get();
            //Obtener solo reportes PENDING
            List<QuestionReportEntity> pending = questionReportRepository.findByQuestionAndStatus(question, "PENDING");
            if (!pending.isEmpty()) {
                for (QuestionReportEntity r : pending) {
                    r.setStatus("DENIED");
                }
                questionReportRepository.saveAll(pending);
            }
            return new Result.Success(200, "All pending reports marked as DENIED");
        } catch (Exception ex) {
            return new Result.InternalError(500, "Internal error denying reports: " + ex.getMessage());
        }
    }
}