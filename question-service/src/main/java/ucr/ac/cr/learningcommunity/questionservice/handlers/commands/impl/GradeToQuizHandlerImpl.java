package ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.GradeToQuizRequest;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.GradeToQuizHandler;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.AnswerOptionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.AnswerOptionRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuizRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.util.List;
import java.util.UUID;

@Service
public class GradeToQuizHandlerImpl implements GradeToQuizHandler {
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuizRepository quizRepository;
    int totalQuestions;
    int correctAnswer;

    public GradeToQuizHandlerImpl(QuestionRepository questionRepository, AnswerOptionRepository answerOptionRepository, QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.quizRepository = quizRepository;
    }

    @Transactional
    @Override
    public Result submitQuiz(GradeToQuizRequest gradeToQuizRequest, UUID quizId) {
        validateRequest(quizId);

        if (quizRepository.findById(quizId).isPresent()){
            totalQuestions = quizRepository.countQuestionsByQuizId(quizId);
            correctAnswer = gradeQuiz(gradeToQuizRequest);

            return new Result.Success(calculateScore(correctAnswer, totalQuestions));

        }else{
            return new Result.Error(
                    ErrorCode.QUIZ_NOT_FOUND.getHttpStatus(),
                    ErrorCode.QUIZ_NOT_FOUND.getDefaultMessage()
            );
        }
    }

    private void validateRequest(UUID quizId){
        validateQuiz(quizId);
    }


    private void validateQuiz(UUID quizId){
        if (quizId == null){
            throw validationError("Quiz is required");
        }
    }

    private int gradeQuiz(GradeToQuizRequest request) {
        int totalQuestions = request.questions().size();
        int correctAnswers = 0;

        for (GradeToQuizRequest.QuestionResponse questionResponse : request.questions()) {

            QuestionEntity question = questionRepository.findById(questionResponse.questionID())
                    .orElseThrow(() -> new RuntimeException("Question no found"));

            List<AnswerOptionEntity> answerOptions = answerOptionRepository.findByQuestionId(question.getId());


            // Buscar la respuesta correcta
            AnswerOptionEntity correctAnswer = answerOptions.stream()
                    .filter(AnswerOptionEntity::isCorrect)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Respuesta correcta no encontrada"));


            // Comparar las respuestas seleccionadas por el usuario
            for (Long selectedAnswerId : questionResponse.selectedAnswersId()) {
                // Buscar la respuesta seleccionada por ID
                AnswerOptionEntity selectedAnswer = answerOptionRepository.findById(selectedAnswerId)
                        .orElseThrow(() -> new RuntimeException("Not Found"));

                // Si la respuesta seleccionada es la correcta, sumamos al contador
                if (selectedAnswer.isCorrect()) {
                    correctAnswers++;
                }

            }

        }
        return correctAnswers;
    }

    private int calculateScore(int correctAnswers, int amountOfQuestions){
        return (correctAnswers / amountOfQuestions ) * 100;
    }

    private BaseException validationError(String message){
        return BaseException.exceptionBuilder()
                .code(ErrorCode.VALIDATION_ERROR)
                .message(message)
                .build();
    }


}
