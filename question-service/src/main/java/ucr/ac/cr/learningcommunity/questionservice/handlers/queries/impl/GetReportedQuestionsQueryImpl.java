package ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.*;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetReportedQuestionsQuery;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionReportEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionReportRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GetReportedQuestionsQueryImpl implements GetReportedQuestionsQuery {

    private final QuestionReportRepository questionReportRepository;
    private final QuestionRepository questionRepository;

    public GetReportedQuestionsQueryImpl(QuestionReportRepository questionReportRepository,
                                         QuestionRepository questionRepository) {
        this.questionReportRepository = questionReportRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionWithReportsResponse> getReportedQuestions() {
        //Status = "PENDING"
        List<QuestionEntity> questions = questionReportRepository.findDistinctQuestionWithPendingReports();

        return questions.stream()
                .map(this::mapQuestionWithReports)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionWithReportsResponse getReportedQuestionById(String questionId) {

        if (questionId == null || questionId.isBlank()) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.VALIDATION_ERROR)
                    .message(ErrorCode.VALIDATION_ERROR.getDefaultMessage() + ", questionId is required")
                    .build();
        }
        UUID qid;
        try {
            qid = UUID.fromString(questionId);
        } catch (IllegalArgumentException ex) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.VALIDATION_ERROR)
                    .message(ErrorCode.VALIDATION_ERROR.getDefaultMessage() + ", invalid UUID format: " + questionId)
                    .build();
        }
        QuestionEntity question = questionRepository.findById(qid)
                .orElseThrow(() -> BaseException.exceptionBuilder()
                        .code(ErrorCode.QUESTION_NOT_FOUND)
                        .message("Question not found: " + questionId)
                        .build()
                );

        return mapQuestionWithReports(question);
    }

    private QuestionWithReportsResponse mapQuestionWithReports(QuestionEntity question) {

        ReportedQuestionResponse questionDto = mapQuestionResponse(question);

        List<QuestionReportEntity> reports = questionReportRepository.findByQuestionAndStatus(question, "PENDING");

        List<ReportInfoResponse> reportDtos = reports.stream()
                .map(r -> {
                    UUID userId = null;
                    if (r.getUser() != null) {

                        userId = UUID.fromString(r.getUser().getId());
                    }
                    Date reportedAt = r.getReportedAt();
                    return new ReportInfoResponse(
                            r.getIdReport(),
                            userId,
                            r.getReason(),
                            r.getDescription(),
                            reportedAt
                    );
                })
                .collect(Collectors.toList());

        return new QuestionWithReportsResponse(questionDto, reportDtos);
    }

    private ReportedQuestionResponse mapQuestionResponse(QuestionEntity q) {

        String encodedImage = null;
        byte[] imgBytes = q.getImage();
        if (imgBytes != null && imgBytes.length > 0) {
            String base64 = Base64.getEncoder().encodeToString(imgBytes);
            encodedImage = "data:image/jpeg;base64," + base64;
        }

        List<CategoryResponse> categories = q.getCategories().stream()
                .map(cat -> new CategoryResponse(cat.getId(), cat.getName()))
                .toList();

        List<AnswerResponse> answers = q.getAnswerOptions().stream()
                .map(ans -> new AnswerResponse(ans.getId(), ans.getText(), ans.isCorrect()))
                .toList();

        return new ReportedQuestionResponse(
                q.getId(),
                q.getIsVisible(),
                encodedImage,
                q.getText(),
                q.getExplanation(),
                categories,
                answers,
                q.getCreatedAt(),
                q.getLikes(),
                q.getDislikes()
        );
    }
}
