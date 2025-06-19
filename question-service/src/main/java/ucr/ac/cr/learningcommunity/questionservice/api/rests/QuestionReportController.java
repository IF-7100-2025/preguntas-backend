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
    public ResponseEntity<ApiResponse> denyReports(@PathVariable String questionId) {
        ApiResponse resp = denyQuestionReportsHandler.denyReports(questionId);
        return ResponseEntity.status(resp.status()).body(resp);
    }
}
