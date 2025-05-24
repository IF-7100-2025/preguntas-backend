package ucr.ac.cr.learningcommunity.questionservice.events;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRegisteredEvent {
    private final String userId;
    private final String username;
    private final String email;
    private final String role;
    private final String password;

    public UserRegisteredEvent(
            @JsonProperty("userId") String userId,
            @JsonProperty("username") String username,
            @JsonProperty("email") String email,
            @JsonProperty("role") String role,
            @JsonProperty("password") String password) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }
}