package ucr.ac.cr.learningcommunity.questionservice.api.types.request;

import java.util.List;

public record QuizRequest(
        List<String> categories,
        int totalQuestions
) {
}
