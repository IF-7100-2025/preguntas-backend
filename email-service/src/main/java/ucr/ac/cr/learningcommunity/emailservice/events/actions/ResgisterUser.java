package ucr.ac.cr.learningcommunity.emailservice.events.actions;

public class ResgisterUser {
    private String userId;
    private String username;
    private String email;
    private String role;
    private String password;
    private String verificationCode;

    public ResgisterUser() {}

    public ResgisterUser(String userId, String username, String email, String role, String password, String verificationCode) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.password = password;
        this.verificationCode = verificationCode;
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

    public String getVerificationCode() {
        return verificationCode;
    }
}
