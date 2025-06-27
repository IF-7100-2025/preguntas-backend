package ucr.ac.cr.learningcommunity.questionservice.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.ApiResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.ReportQuestionHandler;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl.ReportQuestionHandlerImpl;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionReportRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportQuestionHandlerTest {

    @Mock
    private QuestionReportRepository questionReportRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReportQuestionHandlerImpl handler;

    private UUID questionId;
    private UserEntity user;
    private ReportQuestionHandler.Command command;

    @BeforeEach
    void setUp() {
        questionId = UUID.randomUUID();

        user = new UserEntity();
        user.setUsername("jdoe");

        command = new ReportQuestionHandler.Command(
                questionId.toString(),
                user.getUsername(),
                "Inappropriate content",
                "This question contains wrong info"
        );
    }

    @Test
    void testReportQuestion() {
        QuestionEntity question = new QuestionEntity();
        question.setId(questionId);

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(questionReportRepository.existsByUserAndQuestionId(user, questionId)).thenReturn(false);
        when(questionReportRepository.countByUserAndReportDate(eq(user), any())).thenReturn(0L);

        var result = handler.reportQuestion(command);

        assertTrue(result instanceof ReportQuestionHandler.Result.Success);
        ReportQuestionHandler.Result.Success success = (ReportQuestionHandler.Result.Success) result;
        assertEquals(success.response(), success.response());
        assertEquals("Report submitted successfully.", success.response().msg());
    }
}