package ucr.ac.cr.learningcommunity.authservice.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPasswordUpdatedEvent {

    private final String userId;
    private final String newPassword;
    public UserPasswordUpdatedEvent(   @JsonProperty("userId") String userId,
                                       @JsonProperty("newPassword") String newPassword) {
        this.userId = userId;
        this.newPassword = newPassword;
    }

    public String getUserId() {
        return userId;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
