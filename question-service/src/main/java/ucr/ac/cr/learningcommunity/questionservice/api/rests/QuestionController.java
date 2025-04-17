package ucr.ac.cr.learningcommunity.questionservice.api.rests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.CategorizeSuggestionRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.QuestionRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.CategoryResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateQuestionHandler;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final IAIntegrationClient iaIntegrationClient;
    private final CreateQuestionHandler createQuestionHandler;

    @Autowired
    public QuestionController(IAIntegrationClient iaIntegrationClient, CreateQuestionHandler createQuestionHandler) {
        this.iaIntegrationClient = iaIntegrationClient;
        this.createQuestionHandler = createQuestionHandler;
    }

    @PostMapping("/suggestions")
    public ResponseEntity<List<CategoryResponse>> getCategorySuggestions(@RequestBody CategorizeSuggestionRequest request) {
        List<CategoryResponse> suggestedCategories = iaIntegrationClient.getCategorySuggestionsFromIA(request.question());
       // falta menejar mejor con results y mapear bien las responses
        return ResponseEntity.ok(suggestedCategories);
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