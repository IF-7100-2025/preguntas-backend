package ucr.ac.cr.learningcommunity.questionservice.api.types.request;

import java.util.List;

public record QuestionRequest(
        String text,
        String imageBase64,
        String explanation,
        List<AnswerOptionRequest> answerOptions,
        List<String> categories
) {
    public record AnswerOptionRequest(
            String text,
            boolean isCorrect
    ) {}
}