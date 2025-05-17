package ucr.ac.cr.learningcommunity.authservice.api.types;


public record AuthenticationRequest(
        String username,
        String password
) {
}