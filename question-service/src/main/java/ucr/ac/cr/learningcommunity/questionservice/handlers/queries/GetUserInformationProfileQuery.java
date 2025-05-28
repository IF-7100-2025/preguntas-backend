package ucr.ac.cr.learningcommunity.questionservice.handlers.queries;

import ucr.ac.cr.learningcommunity.questionservice.api.types.response.UserProfileResponse;

public interface GetUserInformationProfileQuery {
    sealed interface Result {
        record Success(UserProfileResponse userInformationProfile) implements Result {}
        record Error(int status, String message) implements Result {}
    }

    Result getUserInformationProfile(String userId);
}
