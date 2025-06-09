package ucr.ac.cr.learningcommunity.questionservice.api.types.response;

import java.util.List;

public record QuizResponse(
        String status,
        List<Question> questions
) {

    public record Question(
            String username,
            String text,
            byte[] image,
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