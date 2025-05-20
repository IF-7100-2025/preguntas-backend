package ucr.ac.cr.learningcommunity.questionservice.api.rests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.QuizRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.ApiResponse;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.QuizResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateQuestionHandler;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateQuizHandler;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final CreateQuizHandler createQuizHandler;

    @Autowired
    public QuizController(CreateQuizHandler createQuizHandler) {
        this.createQuizHandler = createQuizHandler;
    }

    @PostMapping
    public ResponseEntity<?> createQuiz(@RequestBody QuizRequest request) {
        var result = createQuizHandler.createQuiz(request);

        return switch (result) {
            case CreateQuizHandler.Result.Success success ->
                    ResponseEntity.ok().body(new ApiResponse(success.status(), success.msg()));
            case CreateQuizHandler.Result.Unauthorized unauthorized ->
                    ResponseEntity.status(unauthorized.status()).body(new ApiResponse(unauthorized.status(), unauthorized.msg()));
            case CreateQuizHandler.Result.InternalError internalError ->
                    ResponseEntity.status(internalError.status()).body(new ApiResponse(internalError.status(), internalError.msg()));

        };
    }
}
