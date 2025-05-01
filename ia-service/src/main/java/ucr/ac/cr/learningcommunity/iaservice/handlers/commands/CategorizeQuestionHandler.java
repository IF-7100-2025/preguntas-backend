package ucr.ac.cr.learningcommunity.iaservice.handlers.commands;

import ucr.ac.cr.learningcommunity.iaservice.api.types.response.CategoryResponse;

import java.util.List;

public interface CategorizeQuestionHandler {
    record Command(
            String questionText
    ){}

    sealed interface Result {
        record Success(List<CategoryResponse> categories) implements Result {}
        record Unauthorized(int status, String msg) implements Result {}
        record InternalError(int status, String msg) implements Result {}
        record ValidationError(int status, String msg) implements Result {}
    }

    Result categorizeQuestion(Command command);
}
