package ucr.ac.cr.learningcommunity.authservice.api.rests;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ucr.ac.cr.learningcommunity.authservice.api.types.LoginRequest;
import ucr.ac.cr.learningcommunity.authservice.handlers.query.LoginUserQuery;
import ucr.ac.cr.learningcommunity.authservice.security.JwtUtil;

import java.util.Map;

@RestController
@RequestMapping("/api/public/user")
public class UserController {

    private final LoginUserQuery loginUserQuery;
    private JwtUtil jwtUtil;
    private ObjectMapper objectMapper;

    @Autowired
    public UserController(LoginUserQuery loginUserQuery, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.loginUserQuery = loginUserQuery;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        var result = loginUserQuery.query(request);

        if (result instanceof LoginUserQuery.Result.Success success) {
            var user = success.user();
            try {
                String rolesJson = objectMapper.writeValueAsString(user.role());

                // Generar token con los datos de usuario
                String token = jwtUtil.generateToken(
                        user.userId(),
                        user.username(),
                        user.email(),
                        rolesJson
                );

                return ResponseEntity.ok(Map.of(
                        "token", token
                ));
            } catch (JsonProcessingException e) {
                return ResponseEntity.internalServerError().body("Error generating token");
            }
        }

        if (result instanceof LoginUserQuery.Result.InvalidCredentials) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        return ResponseEntity.status(404).body("User not found");
    }

}