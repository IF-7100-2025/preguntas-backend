package ucr.ac.cr.learningcommunity.gateway.config;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouterValidator {

    public static final Predicate<ServerHttpRequest> isAnonymous = req ->
            req.getURI().getPath().startsWith("/api/public");

    //Poner aquí las rutas privadas de cada endpoint y el role que admite
    private static final Map<String, List<String>> roleRequired = Map.of(
            "/api/private/ia", List.of("COLAB", "ADMIN"),
            "/api/private/questions/categories", List.of("COLAB", "ADMIN"),
            "/api/private/questions/collaborator", List.of("COLAB", "ADMIN"),
            "/api/private/questions", List.of("COLAB", "ADMIN"),
            "/api/private/questions/quizzes", List.of("COLAB", "ADMIN")
    );


    public Predicate<ServerHttpRequest> isProtected = req ->
            roleRequired.keySet().stream()
                    .anyMatch(prefix -> req.getURI().getPath().startsWith(prefix));

    public List<String> requiredRoles(ServerHttpRequest req) {
        return roleRequired.entrySet().stream()
                .filter(e -> req.getURI().getPath().startsWith(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

}
