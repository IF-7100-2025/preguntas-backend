package ucr.ac.cr.learningcommunity.authservice.api.types;


public record RegisterRequest(
        String username,
        String email,
        String password
) {
}

