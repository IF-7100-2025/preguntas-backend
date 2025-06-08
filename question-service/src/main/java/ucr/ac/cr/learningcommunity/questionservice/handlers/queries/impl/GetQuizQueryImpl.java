package ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl;

import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.QuizResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetCategoriesQuery;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetQuizQuery;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.AnswerOptionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.CategoryEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuizEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuizRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.util.*;

@Service
public class GetQuizQueryImpl implements GetQuizQuery {
    private final QuizRepository quizRepository;

    public GetQuizQueryImpl(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    public Result query(UUID id) {
        // Comprobar si el quiz existe
        if (quizRepository.findById(id).isPresent()) {
            // Si existe, obtener el quiz
            QuizEntity quiz = quizRepository.findById(id).get();

            // Mapeamos el quiz a un QuizResponse
            QuizResponse response = mapToQuizResponse(quiz);

            // Devolver el resultado exitoso con el quiz
            return new Result.Success(response);
        } else {
            // Si no se encuentra el quiz, retornar error
            return new Result.Error(
                    ErrorCode.QUIZ_NOT_FOUND.getHttpStatus(),
                    ErrorCode.QUIZ_NOT_FOUND.getDefaultMessage()
            );
        }
    }

    private QuizResponse mapToQuizResponse(QuizEntity quiz) {
        List<QuizResponse.Question> questions = quiz.getQuestions().stream()
                .map(question -> {
                    UserEntity userEntity = question.getCreatedBy();

                    String username = userEntity.getUsername();


                    List<QuizResponse.Category> categories = new ArrayList<>();
                    for (CategoryEntity category : question.getCategories()) {
                        categories.add(new QuizResponse.Category(category.getName())); // Mapeamos cada categoría
                    }
                    return new QuizResponse.Question(
                            username,
                            question.getText(),
                            question.getImage(),
                            question.getExplanation(),
                            question.getLikes(),
                            question.getDislikes(),
                            categories,
                            mapAnswerOptions(question.getAnswerOptions())
                    );
                })
                .toList();

        return new QuizResponse(
                quiz.getStatus(),
                questions
        );
    }

    private List<QuizResponse.AnswerOption> mapAnswerOptions(Set<AnswerOptionEntity> answerOptions) {
        return new ArrayList<>(answerOptions).stream()
                .map(answerOption -> new QuizResponse.AnswerOption(
                        answerOption.getText(),
                        answerOption.isCorrect()
                ))
                .toList();
    }

    private List<QuizResponse.Category> mapToCategory(Set<CategoryEntity> categories) {
        return categories.stream()
                .map(category -> new QuizResponse.Category(category.getName()))
                .toList();
    }
}

