package ucr.ac.cr.learningcommunity.emailservice.events.actions;

public class UpdatePassword {

    private String userId;
    private  String newPassword;
    public UpdatePassword(String userId, String newPassword) {
        this.userId = userId;
        this.newPassword = newPassword;
    }

    public UpdatePassword() {
    }

    public String getUserId() {
        return userId;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
