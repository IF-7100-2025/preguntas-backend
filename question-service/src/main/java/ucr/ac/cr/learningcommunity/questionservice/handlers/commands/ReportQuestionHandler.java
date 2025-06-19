package ucr.ac.cr.learningcommunity.questionservice.handlers.commands;

import ucr.ac.cr.learningcommunity.questionservice.api.types.request.QuizRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.ApiResponse;

public interface ReportQuestionHandler {
    record Command(String question_id, String username, String reason, String comment, String reported_at) {}
    sealed interface Result{
        record Success(ApiResponse response) implements ReportQuestionHandler.Result {}
        record Error(ApiResponse errorResponse) implements ReportQuestionHandler.Result {}

    }
    ReportQuestionHandler.Result reportQuestion(Command command);
}
