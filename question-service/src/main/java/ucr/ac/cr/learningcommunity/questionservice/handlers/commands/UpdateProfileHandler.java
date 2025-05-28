package ucr.ac.cr.learningcommunity.questionservice.handlers.commands;

import ucr.ac.cr.learningcommunity.questionservice.api.types.request.UpdateProfileRequest;

public interface UpdateProfileHandler {
sealed interface Result {
        record Success(String message) implements Result {}
        record Error(int status, String message) implements Result {}
    }
    Result updateProfile(String userId, UpdateProfileRequest request);
}