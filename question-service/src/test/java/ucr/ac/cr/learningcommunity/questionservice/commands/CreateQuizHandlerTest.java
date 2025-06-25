package ucr.ac.cr.learningcommunity.questionservice.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.QuizRequest;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateQuizHandler;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl.CreateQuizHandlerImpl;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuizEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateQuizHandlerTest {

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private AnswerOptionRepository answerOptionRepository;
    @Mock
    private QuizRepository quizRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateQuizHandlerImpl createQuizHandler;

    private List<String> categories;
    private String userId;
    private UserEntity user;
    private List<QuestionEntity> questions;
    private QuizRequest request;

    @BeforeEach
    void setup() {
        userId = "mariaramos01";
        categories = List.of("Geografía");
        int totalQuestions = 5;

        user = new UserEntity();
        user.setId(userId);

        questions = new ArrayList<>();
        for (int i = 0; i < totalQuestions + 2; i++) {
            QuestionEntity q = new QuestionEntity();
            q.setId(UUID.randomUUID());
            questions.add(q);
        }

        request = new QuizRequest(categories, totalQuestions);
    }

    @Test
    void createQuizSuccessfully() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questionRepository.findByCategoryNames(categories)).thenReturn(questions);
        when(quizRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = createQuizHandler.createQuiz(request, userId);

        assertInstanceOf(CreateQuizHandler.Result.Success.class, result);
        CreateQuizHandler.Result.Success success = (CreateQuizHandler.Result.Success) result;
        assertTrue(success.msg().contains("Quiz created successfully"));

        verify(quizRepository).save(any(QuizEntity.class));
    }

    @Test
    void returnQuestionsRandomSuccessfully() {
        List<String> categories = List.of("Matemáticas", "Ciencias");
        int totalQuestions = 5;

        List<QuestionEntity> allQuestions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            QuestionEntity q = new QuestionEntity();
            q.setId(UUID.randomUUID());
            allQuestions.add(q);
        }

        when(questionRepository.findByCategoryNames(categories)).thenReturn(allQuestions);

        Set<QuestionEntity> result = createQuizHandler.returnQuestionsRandom(categories, totalQuestions);

        assertNotNull(result);
        assertEquals(totalQuestions, result.size());
        verify(questionRepository).findByCategoryNames(categories);
    }
}