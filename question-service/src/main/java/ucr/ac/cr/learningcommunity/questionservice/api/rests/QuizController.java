package ucr.ac.cr.learningcommunity.questionservice.api.rests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.GradeToQuizRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.QuizRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.ApiResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateQuizHandler;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.GradeToQuizHandler;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetQuizQuery;

import java.util.UUID;

@RestController
@RequestMapping("/api/private/questions/quizzes")
public class QuizController {

    private final CreateQuizHandler createQuizHandler;
    private final GetQuizQuery getQuizQuery;
    private final GradeToQuizHandler gradeToQuizHandler;

    @Autowired
    public QuizController(CreateQuizHandler createQuizHandler, GetQuizQuery getQuizQuery, GradeToQuizHandler gradeToQuizHandler) {
        this.createQuizHandler = createQuizHandler;
        this.getQuizQuery = getQuizQuery;
        this.gradeToQuizHandler = gradeToQuizHandler;
    }

    @PostMapping
    public ResponseEntity<?> createQuiz(@RequestBody QuizRequest request,
                                        @RequestHeader("id") String id) {
        var result = createQuizHandler.createQuiz(request, id);

        return switch (result) {
            case CreateQuizHandler.Result.Success success ->
                    ResponseEntity.ok().body(new ApiResponse(success.status(), success.msg()));
            case CreateQuizHandler.Result.Unauthorized unauthorized ->
                    ResponseEntity.status(unauthorized.status()).body(new ApiResponse(unauthorized.status(), unauthorized.msg()));
            case CreateQuizHandler.Result.InternalError internalError ->
                    ResponseEntity.status(internalError.status()).body(new ApiResponse(internalError.status(), internalError.msg()));

        };
    }

    @GetMapping("/{id_quiz}")
    public ResponseEntity<?> getQuizById(@PathVariable("id_quiz") UUID id_quiz) {

        var result = getQuizQuery.query(id_quiz);

        return switch (result) {
            case GetQuizQuery.Result.Success success->
                    ResponseEntity.ok().body(success.quizResponse());
            case GetQuizQuery.Result.Error error->
                    ResponseEntity.status(404).body(new ApiResponse(error.status(), error.message()));
        };

    }

    @PostMapping("endtest/{id_quiz}")
    public ResponseEntity<?> gradeToQuiz(@PathVariable("id_quiz") UUID id_quiz,
                                         @RequestBody GradeToQuizRequest request) {

        var result = gradeToQuizHandler.submitQuiz(request, id_quiz);

        return switch (result) {
            case GradeToQuizHandler.Result.Success success ->
                    ResponseEntity.ok().body((success.gradeToQuizResponse()));
            case GradeToQuizHandler.Result.Error error ->
                    ResponseEntity.status(400).body(new ApiResponse(error.status(), error.message()));
        };
    }
}
