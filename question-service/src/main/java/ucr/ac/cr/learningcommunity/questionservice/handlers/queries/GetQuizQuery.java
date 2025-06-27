package ucr.ac.cr.learningcommunity.questionservice.handlers.queries;

import ucr.ac.cr.learningcommunity.questionservice.api.types.response.QuizResponse;

import java.util.UUID;

public interface GetQuizQuery {

    sealed interface Result{
        record Success(QuizResponse quizResponse) implements Result {}
        record Error(int status, String message) implements Result{}
    }

    Result query(UUID id);
}
