package ucr.ac.cr.learningcommunity.questionservice.api.types.response;

import java.util.List;
import java.util.UUID;

public record GradeToQuizResponse (
        UUID id,
        int grade,
        List<QuestionResult> questionResults
) {

    public record QuestionResult(
            UUID questionId,
            String question,
            List<Option> selectedOptions,
            List<Option> correctOptions,
            String explanation
    ) {
        public record Option(Long id, String answerText){}
    }

}