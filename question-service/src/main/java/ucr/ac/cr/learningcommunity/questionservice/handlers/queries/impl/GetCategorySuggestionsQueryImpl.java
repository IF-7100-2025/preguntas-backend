package ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl;

import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.questionservice.api.rests.IAIntegrationClient;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.CategoryResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetCategorySuggestionsQuery;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.util.List;

@Service
public class GetCategorySuggestionsQueryImpl implements GetCategorySuggestionsQuery {

    private final IAIntegrationClient iaIntegrationClient;

    public GetCategorySuggestionsQueryImpl(IAIntegrationClient iaIntegrationClient) {
        this.iaIntegrationClient = iaIntegrationClient;
    }

    @Override
    public Result getCategorySuggestions(String question) throws BaseException {
        try {
            List<CategoryResponse> suggestions = iaIntegrationClient.getCategorySuggestionsFromIA(question);
            if (suggestions.isEmpty()) {
                return new Result.SuggestionNotFound(204, "Category suggestions not found");
            }
            return new Result.Success(suggestions);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.IA_SERVICE_ERROR)
                    .message("Unexpected error getting category suggestions: " + e.getMessage())
                    .build();
        }
    }
}