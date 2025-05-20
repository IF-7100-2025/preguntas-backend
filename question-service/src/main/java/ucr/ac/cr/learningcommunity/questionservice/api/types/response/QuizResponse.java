package ucr.ac.cr.learningcommunity.questionservice.api.types.response;

import java.util.UUID;

public record QuizResponse(int status, String msg, UUID quizId) {
}
