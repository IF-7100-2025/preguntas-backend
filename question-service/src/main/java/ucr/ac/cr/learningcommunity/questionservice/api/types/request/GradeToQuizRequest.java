package ucr.ac.cr.learningcommunity.questionservice.api.types.request;

import java.util.List;
import java.util.UUID;

public record GradeToQuizRequest(
        UUID quizId,
        List<QuestionResponse> questions
) {

public record QuestionResponse(
        UUID questionID,
        List<Long> selectedAnswersId // ID de las respuestas seleccionadas por el usuario
) {}
}
