package ucr.ac.cr.learningcommunity.questionservice.api.types.response;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record ReportedQuestionResponse(
        UUID id,
        Boolean isVisible,
        String imageBase64,
        String text,
        String explanation,
        List<CategoryResponse> categories,
        List<AnswerResponse> answerOptions,
        Date createdAt,
        Integer likes,
        Integer dislikes
) {}