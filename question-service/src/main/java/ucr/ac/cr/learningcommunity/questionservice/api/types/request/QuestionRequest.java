package ucr.ac.cr.learningcommunity.questionservice.api.types.request;

import java.util.List;

public record QuestionRequest(
        String question,
        String imageBase64,
        List<String> categories
){}
