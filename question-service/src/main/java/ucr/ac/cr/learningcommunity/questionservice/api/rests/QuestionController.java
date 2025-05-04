package ucr.ac.cr.learningcommunity.questionservice.api.rests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.CategorizeSuggestionRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.QuestionRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.ApiResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateQuestionHandler;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetCategorySuggestionsQuery;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final GetCategorySuggestionsQuery suggestionsHandler;
    private final CreateQuestionHandler createQuestionHandler;

    @Autowired
    public QuestionController(
            GetCategorySuggestionsQuery suggestionsHandler,
            CreateQuestionHandler createQuestionHandler) {
        this.suggestionsHandler = suggestionsHandler;
        this.createQuestionHandler = createQuestionHandler;
    }

    @PostMapping("/suggestions")
    public ResponseEntity<?> getCategorySuggestions(
            @RequestBody CategorizeSuggestionRequest request) {
        var result = suggestionsHandler.getCategorySuggestions(request.question());
        return switch (result) {
            case GetCategorySuggestionsQuery.Result.Success success -> ResponseEntity.ok(success.categories());
            case GetCategorySuggestionsQuery.Result.Unauthorized unauthorized -> ResponseEntity.status(unauthorized.status()).body(unauthorized.msg());
            case GetCategorySuggestionsQuery.Result.InternalError internalError -> ResponseEntity.status(internalError.status()).body(internalError.msg());
        };
    }
    @PostMapping
    public ResponseEntity<?> createQuestion(@RequestBody QuestionRequest request) {
        var result = createQuestionHandler.createQuestion(request);
       return switch (result) {
            case CreateQuestionHandler.Result.Success success ->  ResponseEntity.ok().body(new ApiResponse(success.status(), success.msg()));
            case CreateQuestionHandler.Result.Unauthorized unauthorized ->  ResponseEntity.status(unauthorized.status()).body(new ApiResponse(unauthorized.status(), unauthorized.msg()));
            case CreateQuestionHandler.Result.InternalError internalError ->  ResponseEntity.status(internalError.status()).body(new ApiResponse(internalError.status(), internalError.msg()));
        };
    }
}