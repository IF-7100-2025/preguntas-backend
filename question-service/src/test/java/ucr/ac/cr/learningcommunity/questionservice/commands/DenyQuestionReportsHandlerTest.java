package ucr.ac.cr.learningcommunity.questionservice.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.ApiResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl.DenyQuestionReportsHandlerImpl;
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
public class DenyQuestionReportsHandlerTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionReportRepository questionReportRepository;

    @InjectMocks
    private DenyQuestionReportsHandlerImpl denyReportsHandler;

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
    void testDenyReports() {

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(questionReportRepository.findByQuestionAndStatus(question, "PENDING")).thenReturn(pendingReports);

        ApiResponse response = denyReportsHandler.denyReports(questionId.toString());

        assertEquals(HttpStatus.OK.value(), response.status());
        assertEquals("All pending reports marked as DENIED", response.msg());

        for (QuestionReportEntity r : pendingReports) {
            assertEquals("DENIED", r.getStatus());
        }

        verify(questionReportRepository).saveAll(pendingReports);
    }

    @Test
    void testDenyReportsWithNullId() {
        ApiResponse response = denyReportsHandler.denyReports(null);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("questionId is required", response.msg());
    }

    @Test
    void testDenyReportsWithInvalidIdFormat() {
        String invalidId = "not-a-uuid";

        ApiResponse response = denyReportsHandler.denyReports(invalidId);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("Invalid questionId format: " + invalidId, response.msg());
    }
}