package ucr.ac.cr.learningcommunity.questionservice.handlers.commands;

import java.util.UUID;

public interface GradeToQuizHandler {
    sealed interface Result {
        record Succes(int status, String message) implements Result {}
        record Error(int status, String message) implements Result {}
    }
    Result SubmitQuiz(String userId, UUID quizId)
}
