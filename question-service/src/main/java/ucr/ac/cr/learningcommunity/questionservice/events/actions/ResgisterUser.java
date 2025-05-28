package ucr.ac.cr.learningcommunity.questionservice.events.actions;

public class ResgisterUser {
    private String userId;
    private String username;
    private String email;
    private String role;
    private String password;

    public ResgisterUser() {}

    public ResgisterUser(String userId, String username, String email, String role, String password) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.password = password;
    }
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