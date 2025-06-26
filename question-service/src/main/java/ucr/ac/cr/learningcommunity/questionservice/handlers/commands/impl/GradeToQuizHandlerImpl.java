package ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.GradeToQuizRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.GradeToQuizResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.GradeToQuizHandler;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.AnswerOptionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuizEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.*;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class GradeToQuizHandlerImpl implements GradeToQuizHandler {
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final RankRepository rankRepository;
    private final QuestionReportRepository questionReportRepository;

    int totalQuestions;
    int correctAnswer;

    private static final int XP_PER_QUIZ_COMPLETED = 50;

    public GradeToQuizHandlerImpl(
            QuestionRepository questionRepository,
            AnswerOptionRepository answerOptionRepository,
            QuizRepository quizRepository,
            UserRepository userRepository,
            RankRepository rankRepository,
            QuestionReportRepository questionReportRepository

    ) {
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.rankRepository = rankRepository;
        this.questionReportRepository = questionReportRepository;

    }

    @Transactional
    @Override
    public Result submitQuiz(GradeToQuizRequest gradeToQuizRequest, UUID quizId) {

        validateRequest(quizId);

        Optional<QuizEntity> optionalQuiz = quizRepository.findById(quizId);
        if (optionalQuiz.isEmpty()) {
            return new Result.Error(
                    ErrorCode.QUIZ_NOT_FOUND.getHttpStatus(),
                    ErrorCode.QUIZ_NOT_FOUND.getDefaultMessage()
            );
        }

        QuizEntity quiz = optionalQuiz.get();
        if ("completed".equals(quiz.getStatus())) {
            return new Result.Error(
                    ErrorCode.VALIDATION_ERROR.getHttpStatus(),
                    "Quiz already completed. No XP added."
            );
        }

        List<GradeToQuizRequest.QuestionResponse> validResponses = gradeToQuizRequest.questions().stream()
                .filter(qr -> {
                    QuestionEntity q = getQuestionById(qr.questionID(), quizId);
                    boolean hasPendingOrInvisible = !q.getIsVisible()
                            || !questionReportRepository.findByQuestionAndStatus(q, "PENDING").isEmpty();
                    return !hasPendingOrInvisible;
                })
                .toList();

        totalQuestions = validResponses.size();
        GradeToQuizRequest filteredRequest = new GradeToQuizRequest(validResponses);
        correctAnswer = gradeQuiz(filteredRequest, quizId);

        int score = calculateScore(correctAnswer, totalQuestions);
        quiz.setGrade(score);
        quiz.setEndTime(LocalDateTime.now());
        quizRepository.save(quiz);

        UserEntity user = quiz.getCreatedBy();
        if (user != null) {
            user.setXpAmount(user.getXpAmount() + XP_PER_QUIZ_COMPLETED);
            UserEntity refreshed = userRepository.findById(user.getId())
                    .orElseThrow(() -> validationError("User not found after XP update"));
            rankRepository.findRankByXp(refreshed.getXpAmount())
                    .ifPresent(r -> refreshed.setCurrentRank(r.getRank()));
            userRepository.save(refreshed);
        }

        GradeToQuizResponse response = mapToGradeToQuizResponse(filteredRequest, quizId);

        stateToQuiz(quizId);

        return new Result.Success(response);
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

            QuestionEntity question = getQuestionById(questionResponse.questionID(), quizId);

            List<AnswerOptionEntity> answerOptions = answerOptionRepository.findByQuestionId(question.getId());
            Set<Long> correctAnswerIds = answerOptions.stream()
                    .filter(AnswerOptionEntity::isCorrect)
                    .map(AnswerOptionEntity::getId)
                    .collect(Collectors.toSet());

            long matches = questionResponse.selectedAnswersId().stream()
                    .filter(correctAnswerIds::contains)
                    .count();

            if (matches == correctAnswerIds.size()
                    && questionResponse.selectedAnswersId().size() == correctAnswerIds.size()) {
                correctAnswers++;
            }
        }
        return correctAnswers;
    }

    private QuestionEntity getQuestionById(UUID questionId, UUID quizId) {
        return questionRepository.findByQuizIdAndId(quizId, questionId)
                .orElseThrow(() -> validationError("Question not found or not associated with the quiz"));
    }


    private int calculateScore(int correctAnswers, int amountOfQuestions){
        if (amountOfQuestions == 0) {
            return 0;
        }
        return (int) (((double) correctAnswers / amountOfQuestions) * 100);
    }

    public GradeToQuizResponse mapToGradeToQuizResponse(GradeToQuizRequest gradeToQuizRequest, UUID quizId) {
        List<GradeToQuizResponse.QuestionResult> questionResults = new ArrayList<>();

        for (GradeToQuizRequest.QuestionResponse questionResponse : gradeToQuizRequest.questions()) {

            QuestionEntity question = getQuestionById(questionResponse.questionID(), quizId);

            List<AnswerOptionEntity> answerOptions = answerOptionRepository.findByQuestionId(question.getId());

            List<GradeToQuizResponse.QuestionResult.Option> correctOptionsDTO = answerOptions.stream()
                    .filter(AnswerOptionEntity::isCorrect)
                    .map(option -> new GradeToQuizResponse.QuestionResult.Option(option.getId(), option.getText()))
                    .toList();

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