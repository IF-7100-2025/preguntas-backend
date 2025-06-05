package ucr.ac.cr.learningcommunity.authservice.handlers.commands;

import org.springframework.kafka.core.KafkaTemplate;
import ucr.ac.cr.learningcommunity.authservice.events.Event;
import ucr.ac.cr.learningcommunity.authservice.events.EventType;
import ucr.ac.cr.learningcommunity.authservice.events.RegisterUserEvent;
import ucr.ac.cr.learningcommunity.authservice.events.actions.ResgisterUser;
import ucr.ac.cr.learningcommunity.authservice.exceptions.BusinessException;
import ucr.ac.cr.learningcommunity.authservice.exceptions.InvalidInputException;
import ucr.ac.cr.learningcommunity.authservice.jpa.entities.Role;
import ucr.ac.cr.learningcommunity.authservice.jpa.entities.User;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.RoleRepository;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ucr.ac.cr.learningcommunity.authservice.redis.RedisService;
import java.util.Set;

@Component
public class RegisterUserHandler {
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private KafkaTemplate<String, Event<?>> kafkaTemplate;

    public record Command(String email, String username, String password) {
    }

    public void register(Command command) {
        validateRequiredFields(command);
        validateExistingUser(command.username(), command.email());

        User user = new User();
        user.setEmail(command.email());
        user.setUsername(command.username());
        user.setPassword(encoder.encode(command.password()));

        Role defaultRole = roleRepository.findByName("COLAB")
                .orElseThrow(() -> new BusinessException("Default role not found"));

        user.setRoles(Set.of(defaultRole));
        User userSaved = repository.save(user);
        publishUserRegisteredEvent(userSaved);
    }

    private void publishUserRegisteredEvent(User user) {
        String userRole = user.getRoles().stream().findFirst().map(Role::getName).orElse("COLAB");


        String code = String.format("%06d", new java.util.Random().nextInt(999999));

        redisService.saveVerificationCode(user.getEmail(), code, 10);


        ResgisterUser registerUserData = new ResgisterUser(
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getEmail(),
                userRole,
                user.getPassword(),
                code
        );

        RegisterUserEvent event = new RegisterUserEvent();
        event.setEventType(EventType.NEWUSER);
        event.setData(registerUserData);

        kafkaTemplate.send("user-registered-topic2", event);
    }

    private void validateExistingUser(String username, String email) {
        if (repository.findByUsername(username).isPresent() || repository.findByEmail(email).isPresent()) {
            throw new BusinessException("User already exists");
        }
    }

    private void validateRequiredFields(Command command) {
        if (command.email() == null) {
            throw new InvalidInputException("email");
        }
        if (command.username() == null) {
            throw new InvalidInputException("username");
        }
        if (command.password() == null) {
            throw new InvalidInputException("password");
        }
    }
}
