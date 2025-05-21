package ucr.ac.cr.learningcommunity.questionservice.handlers.queries;

import ucr.ac.cr.learningcommunity.questionservice.api.types.response.RandomCategoriesResponse;

import java.util.List;

public interface SearchCategoriesQuery {
    sealed interface Result {
        record Success(List<RandomCategoriesResponse> categories) implements Result {}
        record Error(int status, String message) implements Result {}
    }

    Result search(String term);
}
