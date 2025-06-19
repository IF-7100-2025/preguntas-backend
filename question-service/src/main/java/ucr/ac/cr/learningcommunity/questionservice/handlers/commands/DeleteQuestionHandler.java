package ucr.ac.cr.learningcommunity.questionservice.handlers.commands;

public interface DeleteQuestionHandler {

    Result deleteQuestion(String questionId);

    sealed interface Result {
        record Success(int status, String msg) implements Result {}
        record NotFound(int status, String msg) implements Result {}
        record Unauthorized(int status, String msg) implements Result {}
        record InternalError(int status, String msg) implements Result {}
    }
}
