package ucr.ac.cr.learningcommunity.authservice.handlers.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.authservice.events.Event;
import ucr.ac.cr.learningcommunity.authservice.events.EventType;
import ucr.ac.cr.learningcommunity.authservice.events.LoginSuccessEvent;
import ucr.ac.cr.learningcommunity.authservice.events.actions.LoginSuccess;
import ucr.ac.cr.learningcommunity.authservice.exceptions.BusinessException;
import ucr.ac.cr.learningcommunity.authservice.handlers.queries.UserAuthenticationQuery;
import ucr.ac.cr.learningcommunity.authservice.http.JwtService;
import ucr.ac.cr.learningcommunity.authservice.models.AuthenticatedUser;

@Service
public class AuthenticationHandler {

    @Autowired
    private UserAuthenticationQuery userAuthenticationQuery;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ Usamos el KafkaTemplate genérico
    @Autowired
    private KafkaTemplate<String, Event<?>> kafkaTemplate;

    public String authenticateWithJwt(String username, String password) {
        AuthenticatedUser authenticated = authenticate(username, password);

        // Construir los datos del evento
        LoginSuccess loginData = new LoginSuccess(
                authenticated.email(),     // Usar email real
                authenticated.username()   // Nombre visible
        );

        // Crear evento
        LoginSuccessEvent event = new LoginSuccessEvent();
        event.setEventType(EventType.LOGIN_SUCCESS);
        event.setData(loginData);

        // Enviar evento a Kafka
        kafkaTemplate.send("user-login-success-topic2", event);

        // Devolver token JWT
        return jwtService.generateToken(authenticated);
    }

    private AuthenticatedUser authenticate(String username, String password) {
        if (username == null || username.isBlank())
            throw new BusinessException("Username not provided");

        if (password == null || password.isBlank())
            throw new BusinessException("Password not provided");

        var user = userAuthenticationQuery.loadUserByUsername(username);

        if (!user.isVerified())
            throw new BusinessException("You must verify your email before logging in");

        if (!passwordEncoder.matches(password, user.password()))
            throw new BusinessException("Invalid credentials");

        return user;
    }
}
