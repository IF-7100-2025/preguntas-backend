package ucr.ac.cr.learningcommunity.questionservice.api.rests;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.ReportQuestionRequest;

@RestController
@RequestMapping("/api/private/questions/quizzes")
public class ReportsController {

    @Autowired
    public ReportsController() {}


    @PostMapping("/report")
    public ResponseEntity<?> reportQuestion(@RequestBody ReportQuestionRequest request) {


        return ResponseEntity.ok().build();
    }
}
