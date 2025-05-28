package ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.QuizRequest;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateQuizHandler;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuizEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.*;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CreateQuizHandlerImpl implements CreateQuizHandler {
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;

    public CreateQuizHandlerImpl(QuestionRepository questionRepository,
                                 CategoryRepository categoryRepository,
                                 AnswerOptionRepository answerOptionRepository,
                                 QuizRepository quizRepository, UserRepository userRepository) {

        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    @Override
    public Result createQuiz(QuizRequest request, String userId) {
        validateRequest(request, userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> validationError("User not found"));
        QuizEntity quiz = createQuizEntity(request, user);
        quizRepository.save(quiz);

        return new Result.Success(201, "Quiz created successfully with ID:" + quiz.getId());
    }

    private void validateRequest(QuizRequest request, String userId){
        validateUser(userId);
        validateCategories(request.categories());
        validateTotalQuestions(request.totalQuestions());
    }

    private void validateUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw validationError("User id is required");
        }
    }

    private void validateCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            throw validationError("At least one category is required");
        }

        if (categories.size() > 3) {
            throw validationError("Maximum 3 categories allowed");
        }
        if (categories.size() != categories.stream().distinct().count()) {
            throw validationError("Duplicate categories are not allowed");
        }
    }

    private void validateTotalQuestions(int totalQuestions) {
        if (totalQuestions < 5) {
            throw validationError("Quiz must have at least 5 questions");
        }

        if (totalQuestions > 15) {
            throw validationError("Quiz must have at most 15 questions");
        }
    }

    public Set<QuestionEntity> returnQuestionsRandom(List<String> categoryNames, int totalQuestions) {
        List<QuestionEntity> questions = questionRepository.findByCategoryNames(categoryNames);

        if (questions.size() < totalQuestions) { // Total de preguntas de todas las categorías juntas
            throw validationError("Not enough questions available for the requested categories");
        }

        Collections.shuffle(questions);  // mezcla aleatoriamente la lista

        return new HashSet<>(questions.subList(0, totalQuestions));
    }

    private QuizEntity createQuizEntity(QuizRequest request, UserEntity user) {

        QuizEntity quiz = new QuizEntity();
        quiz.setId(UUID.randomUUID());
        quiz.setCreatedBy(user);
        quiz.setGrade(0);
        quiz.setQuestions(returnQuestionsRandom(request.categories(), request.totalQuestions()));
        quiz.setCompleted(null); // por mientras
        quiz.setStatus("progress");
        quiz.setStartTime(LocalDateTime.now());
        quiz.setEndTime(null);

        return quiz;
    }

    private BaseException validationError(String message) {
        return BaseException.exceptionBuilder()
                .code(ErrorCode.VALIDATION_ERROR)
                .message(message)
                .build();
    }
}





