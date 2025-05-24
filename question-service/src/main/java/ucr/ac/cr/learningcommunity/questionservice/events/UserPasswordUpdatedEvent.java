package ucr.ac.cr.learningcommunity.questionservice.events;

public class UserPasswordUpdatedEvent {
    private final String userId;
    private final String newPassword;
    public UserPasswordUpdatedEvent(String userId, String newPassword) {
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
