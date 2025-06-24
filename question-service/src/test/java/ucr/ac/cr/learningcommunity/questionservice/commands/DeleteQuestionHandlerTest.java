package ucr.ac.cr.learningcommunity.questionservice.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.DeleteQuestionHandler;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl.DeleteQuestionHandlerImpl;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionReportEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionReportRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteQuestionHandlerTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionReportRepository questionReportRepository;

    @InjectMocks
    private DeleteQuestionHandlerImpl deleteQuestionHandler;

    private UUID questionId;
    private QuestionEntity question;
    private List<QuestionReportEntity> pendingReports;

    @BeforeEach
    void setUp() {
        questionId = UUID.randomUUID();
        question = new QuestionEntity();
        question.setId(questionId);

        QuestionReportEntity report1 = new QuestionReportEntity();
        report1.setStatus("PENDING");
        QuestionReportEntity report2 = new QuestionReportEntity();
        report2.setStatus("PENDING");

        pendingReports = List.of(report1, report2);
    }

    @Test
    void testDeleteQuestionHappyPath() {
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(questionReportRepository.findByQuestionAndStatus(question, "PENDING")).thenReturn(pendingReports);

        DeleteQuestionHandler.Result result = deleteQuestionHandler.deleteQuestion(questionId.toString());

        assertTrue(result instanceof DeleteQuestionHandler.Result.Success);
        assertEquals(200, ((DeleteQuestionHandler.Result.Success) result).status());
        assertEquals("Question deleted (logical) and pending reports marked REMOVED", ((DeleteQuestionHandler.Result.Success) result).msg());

        assertFalse(question.getIsVisible());

        for (QuestionReportEntity report : pendingReports) {
            assertEquals("REMOVED", report.getStatus());
        }

        verify(questionRepository).save(question);
        verify(questionReportRepository).saveAll(pendingReports);
    }
}