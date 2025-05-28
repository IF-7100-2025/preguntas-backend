package ucr.ac.cr.learningcommunity.questionservice.handlers.commands;

import ucr.ac.cr.learningcommunity.questionservice.api.types.request.ChangePasswordRequest;

public interface ChangePasswordHandler {
    sealed interface Result {
        record Success(String message) implements Result {}
        record Error(int status, String message) implements Result {}
    }
    Result changePassword(String userId, ChangePasswordRequest request);
}
