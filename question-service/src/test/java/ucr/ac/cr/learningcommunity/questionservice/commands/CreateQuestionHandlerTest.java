package ucr.ac.cr.learningcommunity.questionservice.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.QuestionRequest;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateQuestionHandler;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl.CreateQuestionHandlerImpl;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.*;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateQuestionHandlerTest {

    @Mock private QuestionRepository questionRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private AnswerOptionRepository answerOptionRepository;
    @Mock private UserRepository userRepository;
    @Mock private RankRepository rankRepository;

    @InjectMocks
    private CreateQuestionHandlerImpl createQuestionHandler;

    private QuestionRequest questionRequest;
    private UserEntity user;
    private CategoryEntity category1, category2;

    @BeforeEach
    void setup() {
        user = new UserEntity();
        user.setId("a8e9c9f4-15c3-43e3-9018-2f1684510341");
        user.setUsername("esteban_rojas");
        user.setXpAmount(20);

        category1 = new CategoryEntity();
        category1.setId(1L);
        category1.setName("Matemáticas");

        category2 = new CategoryEntity();
        category2.setId(2L);
        category2.setName("Ciencias");

        questionRequest = new QuestionRequest(
                "Cuánto es 5 + 5?",
                null,
                "Operación para primaria",
                List.of(
                        new QuestionRequest.AnswerOptionRequest("10", true),
                        new QuestionRequest.AnswerOptionRequest("9", false)
                ),
                List.of("Matemáticas", "Ciencias")
        );
    }

    @Test
    void successfullyCreatesQuestion() {

        when(userRepository.findById("a8e9c9f4-15c3-43e3-9018-2f1684510341")).thenReturn(Optional.of(user));
        when(categoryRepository.findAllByNameIn(List.of("Matemáticas", "Ciencias"))).thenReturn(List.of(category1, category2));

        QuestionEntity savedQuestion = new QuestionEntity();
        savedQuestion.setId(UUID.randomUUID());
        when(questionRepository.save(any(QuestionEntity.class))).thenReturn(savedQuestion);

        when(userRepository.calculateNewStreak("a8e9c9f4-15c3-43e3-9018-2f1684510341")).thenReturn(1);
        when(userRepository.findById("a8e9c9f4-15c3-43e3-9018-2f1684510341")).thenReturn(Optional.of(user));

        RankEntity rank = new RankEntity("Aprendiz", 0, 100);
        when(rankRepository.findRankByXp(anyInt())).thenReturn(Optional.of(rank));

        var result = createQuestionHandler.createQuestion(questionRequest, "a8e9c9f4-15c3-43e3-9018-2f1684510341");

        assertTrue(result instanceof CreateQuestionHandler.Result.Success);
        assertEquals("Question created successfully", ((CreateQuestionHandler.Result.Success) result).msg());
        verify(userRepository).updateProgress("a8e9c9f4-15c3-43e3-9018-2f1684510341", 1);
        verify(userRepository).save(user);
    }

    @Test
    void testTextTooShort() {
        questionRequest = new QuestionRequest(
                "Test",
                null,
                "Explicación válida de prueba",
                List.of(
                        new QuestionRequest.AnswerOptionRequest("Respuesta A", true),
                        new QuestionRequest.AnswerOptionRequest("Respuesta B", false)
                ),
                List.of("Matemáticas")
        );

        var ex = assertThrows(RuntimeException.class, () ->
                createQuestionHandler.createQuestion(questionRequest, user.getId())
        );

        assertTrue(ex.getMessage().toLowerCase().contains("question text must be between"));
    }

    @Test
    void testAllAnswersAreCorrect() {
        questionRequest = new QuestionRequest(
                "¿Cuál número es par?",
                null,
                "Explicación válida",
                List.of(
                        new QuestionRequest.AnswerOptionRequest("2", true),
                        new QuestionRequest.AnswerOptionRequest("4", true)
                ),
                List.of("Matemáticas")
        );

        var ex = assertThrows(RuntimeException.class, () ->
                createQuestionHandler.createQuestion(questionRequest, user.getId())
        );

        assertTrue(ex.getMessage().toLowerCase().contains("all answers cannot be correct"));
    }

}