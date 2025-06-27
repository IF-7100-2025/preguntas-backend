package ucr.ac.cr.learningcommunity.authservice.models;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record AuthenticatedUser(
        String id,
        String username,
        String email,
        String password,
        Collection<String> authorities,
        boolean isVerified
) {
    public Map<String, Object> toMap() {
        Map<String, Object> user = new HashMap<>();
        user.put("id", id.toString());
        user.put("email", email);
        user.put("roles", String.join(",", authorities));
        user.put("isVerified", isVerified);
        return user;
    }
}

