package ucr.ac.cr.learningcommunity.questionservice.handlers.queries;

import ucr.ac.cr.learningcommunity.questionservice.api.types.response.UserProgressResponse;

public interface GetProgressQuery {

    Result getProgressUser(String id);


    Result getProgressUserById(String userId);

    sealed interface Result {
        record Success(UserProgressResponse userProgress) implements Result {}
        record Error(int status, String message) implements Result {}
    }
}