package ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.GradeToQuizRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.GradeToQuizResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.GradeToQuizHandler;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.AnswerOptionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.AnswerOptionRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuizRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
            correctAnswer = gradeQuiz(gradeToQuizRequest, quizId);

            GradeToQuizResponse response = mapToGradeToQuizResponse(gradeToQuizRequest, quizId);

            stateToQuiz(quizId);

            return new Result.Success(response);

        }else{
            return new Result.Error(
                    ErrorCode.QUIZ_NOT_FOUND.getHttpStatus(),
                    ErrorCode.QUIZ_NOT_FOUND.getDefaultMessage()
            );
        }
    }

    private void stateToQuiz(UUID quizId){
        quizRepository.updateQuizStatusAndCompletion(quizId, "completed", LocalDateTime.now());
    }

    private void validateRequest(UUID quizId){
        validateQuiz(quizId);
    }

    private void validateQuiz(UUID quizId){
        if (quizId == null){
            throw validationError("Quiz is required");
        }
    }

    private int gradeQuiz(GradeToQuizRequest request, UUID quizId) {
        int correctAnswers = 0;

        for (GradeToQuizRequest.QuestionResponse questionResponse : request.questions()) {

            QuestionEntity question = getQuestionById(questionResponse.questionID(),quizId);

            List<AnswerOptionEntity> answerOptions = answerOptionRepository.findByQuestionId(question.getId());

            Set<Long> correctAnswerIds = answerOptions.stream()
                    .filter(AnswerOptionEntity::isCorrect)
                    .map(AnswerOptionEntity::getId)
                    .collect(Collectors.toSet());

            if (questionResponse.selectedAnswersId().size() != correctAnswerIds.size()) {
                continue;
            }

            for (Long selectedAnswerId : questionResponse.selectedAnswersId()) {
                if (!correctAnswerIds.contains(selectedAnswerId)) {
                    continue;
                }
                correctAnswers++;
            }
        }
        return correctAnswers;
    }

    private QuestionEntity getQuestionById(UUID questionId, UUID quizId) {
        // Buscar la pregunta por su ID y asegurarse de que pertenece al quiz
        return questionRepository.findByQuizIdAndId(quizId, questionId)
                .orElseThrow(() -> validationError("Question not found or not associated with the quiz"));
    }


    private int calculateScore(int correctAnswers, int amountOfQuestions){
        return (int) (((double) correctAnswers / amountOfQuestions) * 100);
    }

    public GradeToQuizResponse mapToGradeToQuizResponse(GradeToQuizRequest gradeToQuizRequest, UUID quizId) {
        List<GradeToQuizResponse.QuestionResult> questionResults = new ArrayList<>();

        for (GradeToQuizRequest.QuestionResponse questionResponse : gradeToQuizRequest.questions()) {

            QuestionEntity question = getQuestionById(questionResponse.questionID(), quizId);

            List<AnswerOptionEntity> answerOptions = answerOptionRepository.findByQuestionId(question.getId());

            List<Long> correctOptionsAmount = answerOptions.stream()
                    .filter(AnswerOptionEntity::isCorrect)
                    .map(AnswerOptionEntity::getId)
                    .toList();

            // correctas en formato record
            List<GradeToQuizResponse.QuestionResult.Option> correctOptionsDTO = answerOptions.stream()
                    .filter(AnswerOptionEntity::isCorrect) // Filtrar solo las opciones correctas
                    .map(option -> new GradeToQuizResponse.QuestionResult.Option(option.getId(), option.getText())) // Mapea las opciones correctas a Option
                    .toList();

            // GradeToQuizResponse.QuestionResult.Option es el record
            List<GradeToQuizResponse.QuestionResult.Option> selectedOptions = answerOptions.stream()
                    .filter(option -> questionResponse.selectedAnswersId().contains(option.getId()))
                    .map(option -> new GradeToQuizResponse.QuestionResult.Option(option.getId(), option.getText()))
                    .toList();

            GradeToQuizResponse.QuestionResult result = new GradeToQuizResponse.QuestionResult(
                    question.getId(),
                    question.getText(),
                    selectedOptions,
                    correctOptionsDTO,
                    question.getExplanation()
            );

            questionResults.add(result);
        }

        return new GradeToQuizResponse(quizId, "completed", calculateScore(correctAnswer, totalQuestions), questionResults);
    }



    private BaseException validationError(String message){
        return BaseException.exceptionBuilder()
                .code(ErrorCode.VALIDATION_ERROR)
                .message(message)
                .build();
    }
}
