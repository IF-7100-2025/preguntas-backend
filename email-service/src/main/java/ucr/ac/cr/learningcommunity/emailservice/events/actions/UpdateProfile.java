package ucr.ac.cr.learningcommunity.emailservice.events.actions;

public class UpdateProfile {
    private  String userId;
    private  String username;
    private  String email;

    public UpdateProfile(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }



    public UpdateProfile() {
    }

    @Override
    public String toString() {
        return "UserProfileUpdatedEvent{" +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}