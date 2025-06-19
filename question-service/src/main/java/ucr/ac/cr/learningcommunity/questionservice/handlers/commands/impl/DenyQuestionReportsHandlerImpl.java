package ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.DenyQuestionReportsHandler;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionReportEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionReportRepository;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.ApiResponse;
import org.springframework.http.HttpStatus;

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
    public ApiResponse denyReports(String questionId) {
        if (questionId == null || questionId.isBlank()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "questionId is required");
        }
        UUID qid;
        try {
            qid = UUID.fromString(questionId);
        } catch (IllegalArgumentException ex) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                    "Invalid questionId format: " + questionId);
        }
        try {
            Optional<QuestionEntity> optQ = questionRepository.findById(qid);
            if (optQ.isEmpty()) {
                return new ApiResponse(HttpStatus.NOT_FOUND.value(),
                        "Question not found: " + questionId);
            }
            QuestionEntity question = optQ.get();
            List<QuestionReportEntity> pending =
                    questionReportRepository.findByQuestionAndStatus(question, "PENDING");
            if (!pending.isEmpty()) {
                for (QuestionReportEntity r : pending) {
                    r.setStatus("DENIED");
                }
                questionReportRepository.saveAll(pending);
            }
            return new ApiResponse(HttpStatus.OK.value(),
                    "All pending reports marked as DENIED");
        } catch (Exception ex) {
            return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal error denying reports");
        }
    }
}
