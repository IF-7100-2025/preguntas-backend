package ucr.ac.cr.learningcommunity.questionservice.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.QuestionResponse;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.*;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl.GetQuestionsQueryImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetQuestionsQueryTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private GetQuestionsQueryImpl getQuestionsQuery;

    private QuestionEntity question;

    @BeforeEach
    void setUp() {
        CategoryEntity category = new CategoryEntity();
        category.setId(1L);
        category.setName("Ciencias");

        AnswerOptionEntity option = new AnswerOptionEntity();
        option.setId(1L);
        option.setText("Mercurio");
        option.setCorrect(true);

        question = new QuestionEntity();
        question.setId(UUID.randomUUID());
        question.setText("¿Cuál es el planeta más cercano al Sol?");
        question.setExplanation("Sistema solar");
        question.setCreatedAt(new Date());
        question.setLikes(10);
        question.setDislikes(2);
        question.setCategories(Set.of(category));
        question.setAnswerOptions(Set.of(option));
    }

    @Test
    public void testGetQuestionsByUserId() {

        when(questionRepository.findByCreatedBy_Id("689a184b-2046-45e2-8ee2-8a00ff203df1")).thenReturn(List.of(question));

        List<QuestionResponse> responses = getQuestionsQuery.getQuestionsByUserId("689a184b-2046-45e2-8ee2-8a00ff203df1");

        assertNotNull(responses);
        assertEquals(1, responses.size());

        QuestionResponse res = responses.get(0);
        assertEquals(question.getId(), res.id());
        assertEquals("¿Cuál es el planeta más cercano al Sol?", res.text());
        assertEquals("Sistema solar", res.explanation());
        assertEquals(1, res.categories().size());
        assertEquals(1, res.answerOptions().size());
        assertEquals(10, res.likes());
        assertEquals(2, res.dislikes());
    }
}