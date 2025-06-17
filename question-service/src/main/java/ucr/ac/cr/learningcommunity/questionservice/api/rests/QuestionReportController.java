package ucr.ac.cr.learningcommunity.questionservice.api.rests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetReportedQuestionsQuery;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.DenyQuestionReportsHandler;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.ApiResponse;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.QuestionWithReportsResponse;

import java.util.List;

@RestController
@RequestMapping("/api/private/questions/reports")
public class QuestionReportController {

    private final GetReportedQuestionsQuery getReportedQuestionsQuery;

    @Autowired
    private DenyQuestionReportsHandler denyQuestionReportsHandler;

    public QuestionReportController(GetReportedQuestionsQuery getReportedQuestionsQuery) {
        this.getReportedQuestionsQuery = getReportedQuestionsQuery;
    }

    @GetMapping
    public ResponseEntity<List<QuestionWithReportsResponse>> listReportedQuestions() {
        List<QuestionWithReportsResponse> list = getReportedQuestionsQuery.getReportedQuestions();
        if (list == null || list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionWithReportsResponse> getReportedQuestionById(
            @PathVariable("questionId") String questionId
    ) {
        QuestionWithReportsResponse dto = getReportedQuestionsQuery.getReportedQuestionById(questionId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/deny/{questionId}")
    public ResponseEntity<ApiResponse> denyReports(
            @PathVariable("questionId") String questionId
    ) {
        var result = denyQuestionReportsHandler.denyReports(questionId);
        return switch (result) {
            case DenyQuestionReportsHandler.Result.Success success ->
                    ResponseEntity.ok(new ApiResponse(success.status(), success.msg()));
            case DenyQuestionReportsHandler.Result.NotFound notFound ->
                    ResponseEntity.status(notFound.status())
                            .body(new ApiResponse(notFound.status(), notFound.msg()));
            case DenyQuestionReportsHandler.Result.InternalError internalError ->
                    ResponseEntity.status(internalError.status())
                            .body(new ApiResponse(internalError.status(), internalError.msg()));
        };
    }
}
