package ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.ApiResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.ReportQuestionHandler;
import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionReportEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionReportRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.UserRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class ReportQuestionHandlerImpl implements ReportQuestionHandler {

    private static final int MAX_DAILY_REPORTS = 10;
    private static final int MAX_COMMENT_LENGTH = 400;

    private final QuestionReportRepository questionReportRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public ReportQuestionHandlerImpl(QuestionReportRepository questionReportRepository,
                                     QuestionRepository questionRepository,
                                     UserRepository userRepository) {
        this.questionReportRepository = questionReportRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Result reportQuestion(Command command) {
        try {
            // Validaciones básicas del request
            if (!isValidCommand(command)) {
                return new Result.Error(
                        new ApiResponse(HttpStatus.BAD_REQUEST.value(),  "Invalid report data.")
                );
            }

            UUID questionId = UUID.fromString(command.question_id());

            QuestionEntity question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new IllegalArgumentException("Question not found"));

            UserEntity user = userRepository.findByUsername(command.username())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            validateReportLimits(user, questionId);
            QuestionReportEntity report = createReportEntity(command, question, user);
            questionReportRepository.save(report);

            return new Result.Success(
                    new ApiResponse(HttpStatus.CREATED.value(),  "Report submitted successfully.")
            );

        } catch (IllegalArgumentException e) {
            // maneja tanto las validaciones como los casos de límite excedido y duplicados
            return new Result.Error(
                    new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        } catch (Exception e) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.ERORR_CREATING_REPORT)
                    .message(ErrorCode.ERORR_CREATING_REPORT.getDefaultMessage() + ": " + e.getMessage())
                    .build();
        }
    }

    private boolean isValidCommand(Command command) {
        // campos obligatorios
        if (command.question_id() == null || command.question_id().isBlank()) {
            return false;
        }

        if (command.username() == null || command.username().isBlank()) {
            return false;
        }

        // obligatorio
        if (command.reason() == null || command.reason().isBlank()) {
            return false;
        }

        // longitud de comentario son 400 caracteres
        if (command.comment() != null && command.comment().length() > MAX_COMMENT_LENGTH) {
            return false;
        }

        return true;
    }

    private void validateReportLimits(UserEntity user, UUID questionId) {
        // Verificar si ya reportó esta pregunta
        boolean alreadyReported = questionReportRepository.existsByUserAndQuestionId(user, questionId);
        if (alreadyReported) {
            throw new IllegalArgumentException("You have already reported this question");
        }

        // Verificar límite diario de reportes
        LocalDate today = LocalDate.now();
        long todayReports = questionReportRepository.countByUserAndReportDate(user, today);
        if (todayReports >= MAX_DAILY_REPORTS) {
            throw new IllegalArgumentException(
                    "You have reached the daily limit of " + MAX_DAILY_REPORTS + " reports");
        }
    }

    private QuestionReportEntity createReportEntity(Command command, QuestionEntity question, UserEntity user) {
        QuestionReportEntity report = new QuestionReportEntity();
        report.setIdReport(UUID.randomUUID());
        report.setQuestion(question);
        report.setUser(user);
        report.setStatus("PENDING");
        report.setReason(command.reason());

        // Comentario opcional
        if (command.comment() != null && !command.comment().isBlank()) {
            report.setDescription(command.comment());
        }
        return report;
    }
}
