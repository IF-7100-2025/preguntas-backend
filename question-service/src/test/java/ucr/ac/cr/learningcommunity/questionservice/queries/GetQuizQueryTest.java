package ucr.ac.cr.learningcommunity.questionservice.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.QuizResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetQuizQuery;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl.GetQuizQueryImpl;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.*;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuizRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetQuizQueryTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private GetQuizQueryImpl getQuizQuery;

    private UUID quizId;
    private QuizEntity quiz;

    @BeforeEach
    void setup() {

        quizId = UUID.randomUUID();
        quiz = new QuizEntity();
        quiz.setId(quizId);
        quiz.setStatus("en progreso");

        UserEntity user = new UserEntity();
        user.setUsername("andrea23");

        CategoryEntity category = new CategoryEntity();
        category.setName("Historia");

        AnswerOptionEntity answerOption = new AnswerOptionEntity();
        answerOption.setId(1L);
        answerOption.setText("1955");

        QuestionEntity question = new QuestionEntity();
        question.setId(UUID.randomUUID());
        question.setText("En qué año se creó el primer lenguaje de programación?");
        question.setCreatedBy(user);
        question.setLikes(5);
        question.setDislikes(1);
        question.setAnswerOptions(Set.of(answerOption));
        question.setCategories(Set.of(category));
        question.setImage(null);

        quiz.setQuestions(Set.of(question));
    }

    @Test
    void testReturnsQuizResponse() {

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        var result = getQuizQuery.query(quizId);

        assertInstanceOf(GetQuizQuery.Result.Success.class, result);
        GetQuizQuery.Result.Success success = (GetQuizQuery.Result.Success) result;

        QuizResponse response = success.quizResponse();
        assertEquals(quizId, response.quizId());
        assertEquals("en progreso", response.status());
        assertEquals(1, response.questions().size());

        QuizResponse.Question resQuestion = response.questions().get(0);
        assertEquals("andrea23", resQuestion.username());
        assertEquals("En qué año se creó el primer lenguaje de programación?", resQuestion.text());
        assertEquals(1, resQuestion.categories().size());
        assertEquals("Historia", resQuestion.categories().get(0).name());
        assertEquals(1, resQuestion.answerOptions().size());
        assertEquals("1955", resQuestion.answerOptions().get(0).text());
    }

    @Test
    void testReturnsErrorWhenQuizNotFound() {
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        var result = getQuizQuery.query(quizId);

        assertInstanceOf(GetQuizQuery.Result.Error.class, result);
        GetQuizQuery.Result.Error error = (GetQuizQuery.Result.Error) result;
        assertEquals(404, error.status());
        assertEquals("Quiz not found", error.message());
    }

}