package ucr.ac.cr.learningcommunity.questionservice.handlers.commands;

import ucr.ac.cr.learningcommunity.questionservice.api.types.request.GradeToQuizRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.GradeToQuizResponse;

import java.util.UUID;

public interface GradeToQuizHandler {
    sealed interface Result {
        record Success(GradeToQuizResponse gradeToQuizResponse) implements Result {}
        record Error(int status, String message) implements Result {}
    }
    Result submitQuiz(GradeToQuizRequest gradeToQuizRequest, UUID quizId);
}
