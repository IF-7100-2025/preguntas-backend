package ucr.ac.cr.learningcommunity.questionservice.handlers.queries;

import ucr.ac.cr.learningcommunity.questionservice.api.types.response.CategoryResponse;

import java.util.List;

public interface GetCategorySuggestionsQuery {
    Result getCategorySuggestions(String question);

    sealed interface Result {
        record Success(List<CategoryResponse> categories) implements Result {}
        record Unauthorized(int status, String msg) implements Result {}
        record InternalError(int status, String msg) implements Result {}
    }
}
