package ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl;

import org.apache.hc.core5.reactor.Command;
import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.QuizRequest;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateQuizHandler;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuizEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.AnswerOptionRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.CategoryRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuizRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.util.UUID;

@Service
public class CreateQuizHandlerImpl implements CreateQuizHandler {
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuizRepository quizRepository;

    public CreateQuizHandlerImpl(QuestionRepository questionRepository, CategoryRepository categoryRepository, AnswerOptionRepository answerOptionRepository, QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.quizRepository = quizRepository;
    }

    @Override
    public Result createQuiz(Command command) {
        return null;
    }


    private void validateAmountCategories(int amountCategory){
        if (amountCategory == 0) {
            throw validationError("Needs at least 1 category");
        }
    }

    // cuenta cantidad de preguntas de cada categoria
    private void validateAmountQuestions(String categoryName){
        long amountQuestions = questionRepository.countByText(categoryName);
        if (amountQuestions <= 3){
            throw validationError("Category " +  categoryName + " must have more than 2 question");
        }
    }

//    private QuizEntity createQuiz(QuizRequest request){
//        QuizEntity quiz = new QuizEntity();
//        quiz.setId(UUID.randomUUID());
//        quiz.setGrade(0);
//        id_
//
//    }

    private BaseException validationError(String message) {
        return BaseException.exceptionBuilder()
                .code(ErrorCode.VALIDATION_ERROR)
                .message(message)
                .build();
    }
}





