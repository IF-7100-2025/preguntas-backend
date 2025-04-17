package ucr.ac.cr.learningcommunity.questionservice.handlers.commands;


import java.util.List;

public interface CreateQuestionHandler {
    record Command(
            String question,
            String imageBase64,
            List<String> categories
    ){}
    sealed interface Result {
        record Success(int status, String msg) implements Result {}
        record Unauthorized(int status, String msg) implements Result {}
        record InternalError(int status, String msg) implements Result {}
    }

    Result createQuestion(Command command);
}