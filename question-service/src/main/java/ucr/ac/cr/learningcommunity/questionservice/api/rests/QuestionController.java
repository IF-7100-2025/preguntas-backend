package ucr.ac.cr.learningcommunity.questionservice.api.rests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.CategorizeSuggestionRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.ProgressRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.QuestionRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.ApiResponse;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.UserProgressResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateQuestionHandler;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetCategorySuggestionsQuery;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetProgressQuery;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetQuestionsQuery;


@RestController
@RequestMapping("/api/private/questions")
public class QuestionController {

    private final GetCategorySuggestionsQuery suggestionsHandler;
    private final CreateQuestionHandler createQuestionHandler;
    private final GetProgressQuery progressHandler;
    private final GetQuestionsQuery getQuestionsQuery;


    @Autowired
    public QuestionController(
            GetCategorySuggestionsQuery suggestionsHandler,
            CreateQuestionHandler createQuestionHandler,
            GetQuestionsQuery getQuestionsQuery,
            GetProgressQuery progressHandler) {
        this.suggestionsHandler = suggestionsHandler;
        this.createQuestionHandler = createQuestionHandler;
        this.progressHandler = progressHandler;
        this.getQuestionsQuery = getQuestionsQuery;
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
    @GetMapping("/progress")
    public ResponseEntity<?> getUserProgress(@RequestHeader("username") String username) {
        var result = progressHandler.getProgressUser(username);
        return switch (result) {
            case GetProgressQuery.Result.Success success ->
                    ResponseEntity.ok(success.userProgress());
            case GetProgressQuery.Result.Error error ->
                    ResponseEntity.status(error.status())
                            .body(new ApiResponse(error.status(), error.message()));
        };
    }


    @PostMapping
    public ResponseEntity<?> createQuestion(@RequestBody QuestionRequest request,
                                            @RequestHeader("id") String id) {
        var result = createQuestionHandler.createQuestion(request, id);
       return switch (result) {
            case CreateQuestionHandler.Result.Success success ->  ResponseEntity.ok().body(new ApiResponse(success.status(), success.msg()));
            case CreateQuestionHandler.Result.Unauthorized unauthorized ->  ResponseEntity.status(unauthorized.status()).body(new ApiResponse(unauthorized.status(), unauthorized.msg()));
            case CreateQuestionHandler.Result.InternalError internalError ->  ResponseEntity.status(internalError.status()).body(new ApiResponse(internalError.status(), internalError.msg()));
        };
    }

    @GetMapping
    public ResponseEntity<?> getQuestionsByUser(@RequestHeader("id") String userId) {
        try {
            var questions = getQuestionsQuery.getQuestionsByUserId(userId);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error getting questions from user: " + e.getMessage());
        }
}
}