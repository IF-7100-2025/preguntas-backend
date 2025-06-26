package ucr.ac.cr.learningcommunity.questionservice.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.GradeToQuizRequest;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.GradeToQuizHandler;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl.GradeToQuizHandlerImpl;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.*;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GradeToQuizHandlerTest {

    @Mock private QuestionRepository questionRepository;
    @Mock private AnswerOptionRepository answerOptionRepository;
    @Mock private QuizRepository quizRepository;
    @Mock private UserRepository userRepository;
    @Mock private RankRepository rankRepository;

    @InjectMocks
    private GradeToQuizHandlerImpl handler;

    private QuizEntity quiz;
    private UserEntity user;
    private UUID quizId;
    private UUID questionId;
    private GradeToQuizRequest request;

    @BeforeEach
    void setup() {
        quizId = UUID.randomUUID();
        questionId = UUID.randomUUID();

        user = new UserEntity();
        user.setId("a45b2c57-03b4-4970-9021-ccf5dcb6uuff");
        user.setXpAmount(100);
        user.setCurrentRank("Aprendiz");

        quiz = new QuizEntity();
        quiz.setId(quizId);
        quiz.setStatus("pending");
        quiz.setCreatedBy(user);

        request = new GradeToQuizRequest(List.of(
                new GradeToQuizRequest.QuestionResponse(questionId, List.of(1L))
        ));
    }

    @Test
    void testGradesQuizAndUpdatesUser() {
        QuestionEntity question = new QuestionEntity();
        question.setId(questionId);
        question.setText("¿Capital de Francia?");
        question.setExplanation("París es la capital de Francia.");

        AnswerOptionEntity correctAnswer = new AnswerOptionEntity();
        correctAnswer.setId(1L);
        correctAnswer.setText("París");
        correctAnswer.setCorrect(true);
        correctAnswer.setQuestion(question);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(quizRepository.countQuestionsByQuizId(quizId)).thenReturn(1);
        when(questionRepository.findByQuizIdAndId(quizId, questionId)).thenReturn(Optional.of(question));
        when(answerOptionRepository.findByQuestionId(questionId)).thenReturn(List.of(correctAnswer));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(rankRepository.findRankByXp(anyInt())).thenReturn(Optional.empty());

        GradeToQuizHandler.Result result = handler.submitQuiz(request, quizId);

        assertInstanceOf(GradeToQuizHandler.Result.Success.class, result);
        GradeToQuizHandler.Result.Success success = (GradeToQuizHandler.Result.Success) result;
        assertEquals(100, success.gradeToQuizResponse().grade());
        assertEquals("completed", success.gradeToQuizResponse().status());
    }

    @Test
    void submitQuizFailsWhenQuizNotFound() {
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        GradeToQuizHandler.Result result = handler.submitQuiz(request, quizId);

        assertInstanceOf(GradeToQuizHandler.Result.Error.class, result);
        GradeToQuizHandler.Result.Error error = (GradeToQuizHandler.Result.Error) result;
        assertEquals(404, error.status());
        assertTrue(error.message().toLowerCase().contains("quiz not found"));
    }

    @Test
    void submitQuizFailsWhenQuestionNotInQuiz() {
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(quizRepository.countQuestionsByQuizId(quizId)).thenReturn(1);
        when(questionRepository.findByQuizIdAndId(quizId, questionId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                handler.submitQuiz(request, quizId)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("question not found"));
    }
}