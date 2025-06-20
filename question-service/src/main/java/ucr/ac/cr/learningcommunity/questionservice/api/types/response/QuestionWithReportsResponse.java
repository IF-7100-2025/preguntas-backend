package ucr.ac.cr.learningcommunity.questionservice.api.types.response;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record QuestionWithReportsResponse(
        ReportedQuestionResponse question,
        List<ReportInfoResponse> reports
) {}


