package ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl;

import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.GradeToQuiz;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.GradeToQuizRequest;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.AnswerOptionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.AnswerOptionRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.util.List;

@Service
public class GradeToQuizHandlerImpl {
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;

    public GradeToQuizHandlerImpl(QuestionRepository questionRepository, AnswerOptionRepository answerOptionRepository, QuestionEntity question) {
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
    }

    public void gradeQuiz(GradeToQuizRequest request){
        int totalQuestions = request.questions().size();
        int correctAnswers = 0;

        for(GradeToQuizRequest.QuestionResponse questionResponse : request.questions()){

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



    private BaseException validationError(String message) {
        return BaseException.exceptionBuilder()
                .code(ErrorCode.VALIDATION_ERROR)
                .message(message)
                .build();
    }
}
