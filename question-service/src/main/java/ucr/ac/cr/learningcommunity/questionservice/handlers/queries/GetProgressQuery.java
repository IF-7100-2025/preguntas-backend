package ucr.ac.cr.learningcommunity.questionservice.handlers.queries;

import ucr.ac.cr.learningcommunity.questionservice.api.types.response.UserProgressResponse;

public interface GetProgressQuery {
    sealed interface Result{
        record Success(UserProgressResponse userProgress) implements Result{}//debo cambiar el response
        record Error(int status, String message) implements Result {}
    }
    Result getProgressUser(String id);
}
