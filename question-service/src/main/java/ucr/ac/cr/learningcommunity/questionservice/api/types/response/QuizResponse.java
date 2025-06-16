package ucr.ac.cr.learningcommunity.questionservice.api.types.response;

import java.util.List;
import java.util.UUID;

public record QuizResponse(
        UUID id,
        String status,
        List<Question> questions
) {

    public record Question(
            String username,
            String text,
            String image,
            int like,
            int dislike,
            List<Category> categories,
            List<AnswerOption> answerOptions
    ) {}

    public record Category(
            String name
    ) {}

    public record AnswerOption(
            String text
    ) {}
}