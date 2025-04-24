package ucr.ac.cr.learningcommunity.questionservice.api.rests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.CategorizeSuggestionRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.QuestionRequest;
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
            case GetCategorySuggestionsQuery.Result.CategorySuggestionOutOfService categorySuggestionOutOfService -> ResponseEntity.status(categorySuggestionOutOfService.status()).body(categorySuggestionOutOfService.msg());
            case GetCategorySuggestionsQuery.Result.SuggestionNotFound suggestionNotFound -> ResponseEntity.status(suggestionNotFound.status()).body(suggestionNotFound.msg());
        };
    }
    @PostMapping
    public ResponseEntity<Object> createQuestion(@RequestBody QuestionRequest request) {
        var result = createQuestionHandler.createQuestion(
                new CreateQuestionHandler.Command(request.question(),
                       request.imageBase64(),
                       request.categories()));
        switch (result) {
            case CreateQuestionHandler.Result.Success success ->  ResponseEntity.ok().body(success.msg());
            case CreateQuestionHandler.Result.Unauthorized unauthorized ->  ResponseEntity.status(unauthorized.status()).body(unauthorized.msg());
            case CreateQuestionHandler.Result.InternalError internalError ->  ResponseEntity.status(internalError.status()).body(internalError.msg());
        }
        return ResponseEntity.ok(result);
    }
}