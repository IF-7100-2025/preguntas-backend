package ucr.ac.cr.learningcommunity.questionservice.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.QuestionWithReportsResponse;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.*;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionReportRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl.GetReportedQuestionsQueryImpl;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetReportedQuestionsQueryTest {

    @Mock
    private QuestionReportRepository questionReportRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private GetReportedQuestionsQueryImpl getReportedQuestionsQuery;

    private QuestionEntity question;
    private QuestionReportEntity report;

    @BeforeEach
    public void setUp() {
        question = new QuestionEntity();
        question.setId(UUID.randomUUID());
        question.setText("Qué es Java?");
        question.setExplanation("Lenguajes de programación empleados");
        question.setIsVisible(true);
        question.setCreatedAt(new Date());
        question.setLikes(10);
        question.setDislikes(1);
        question.setImage("Sample image".getBytes());

        CategoryEntity category = new CategoryEntity();
        category.setId(1L);
        category.setName("Programación");
        question.setCategories(Set.of(category));

        AnswerOptionEntity answer = new AnswerOptionEntity();
        answer.setId(1L);
        answer.setText("Una receta de cocina");
        answer.setCorrect(true);
        question.setAnswerOptions(Set.of(answer));

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID().toString());

        report = new QuestionReportEntity();
        report.setIdReport(UUID.randomUUID());
        report.setQuestion(question);
        report.setStatus("PENDING");
        report.setUser(user);
        report.setReason("La pregunta posee una respuesta incorrecta");
        report.setDescription("La explicacion no concuerda con la respuesta");
    }

    @Test
    public void testGetReportedQuestions() {

        when(questionReportRepository.findDistinctQuestionWithPendingReports())
                .thenReturn(List.of(question));
        when(questionReportRepository.findByQuestionAndStatus(question, "PENDING"))
                .thenReturn(List.of(report));

        List<QuestionWithReportsResponse> result = getReportedQuestionsQuery.getReportedQuestions();

        assertEquals(1, result.size());

        var questionResponse = result.get(0).question();
        assertEquals(question.getText(), questionResponse.text());
        assertEquals(question.getExplanation(), questionResponse.explanation());
        assertEquals(1, questionResponse.categories().size());
        assertEquals(1, questionResponse.answerOptions().size());

        var reports = result.get(0).reports();
        assertEquals(1, reports.size());
        assertEquals(report.getReason(), reports.get(0).reason());
        assertEquals(report.getDescription(), reports.get(0).description());
    }

    @Test
    public void testGetReportedQuestionById() {

        question.setId(UUID.randomUUID());
        report.setQuestion(question);
        report.setStatus("PENDING");

        UUID questionId = question.getId();
        String questionIdStr = questionId.toString();

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(questionReportRepository.findByQuestionAndStatus(question, "PENDING")).thenReturn(List.of(report));

        QuestionWithReportsResponse result = getReportedQuestionsQuery.getReportedQuestionById(questionIdStr);

        assertNotNull(result);

        var questionDto = result.question();
        assertEquals(question.getId(), questionDto.id());
        assertEquals(question.getText(), questionDto.text());
        assertEquals(question.getExplanation(), questionDto.explanation());
        assertEquals(question.getLikes(), questionDto.likes());
        assertEquals(question.getDislikes(), questionDto.dislikes());
        assertEquals(question.getIsVisible(), questionDto.isVisible());
        assertEquals(1, questionDto.categories().size());
        assertEquals(1, questionDto.answerOptions().size());

        var reports = result.reports();
        assertEquals(1, reports.size());
        assertEquals(report.getReason(), reports.get(0).reason());
        assertEquals(report.getDescription(), reports.get(0).description());
        assertEquals(report.getUser().getId(), reports.get(0).userId().toString());

        verify(questionRepository, times(1)).findById(questionId);
        verify(questionReportRepository, times(1)).findByQuestionAndStatus(question, "PENDING");
    }

    @Test
    void testInvalidUUID() {
        var invalidId = "invalid-uuid-format";

        var exception = assertThrows(BaseException.class, () -> {
            getReportedQuestionsQuery.getReportedQuestionById(invalidId);
        });

        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("invalid UUID format"));
    }

}