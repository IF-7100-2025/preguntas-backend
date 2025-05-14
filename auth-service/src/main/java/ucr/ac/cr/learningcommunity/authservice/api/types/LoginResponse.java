// LoginResponse.java
package ucr.ac.cr.learningcommunity.authservice.api.types;

public record LoginResponse(String userId, String username, String email, java.util.Set<ucr.ac.cr.learningcommunity.authservice.jpa.entities.Role> role) {}
