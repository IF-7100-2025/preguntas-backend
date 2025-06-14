package ucr.ac.cr.learningcommunity.questionservice.api.types.response;

import java.util.List;
import java.util.UUID;
import java.util.Date;

public record QuestionResponse(
        UUID id,
        String text,
        String explanation,
        List<CategoryResponse> categories,
        List<AnswerResponse> answerOptions,
        Date createdAt,
        Integer likes,
        Integer dislikes
) {}
