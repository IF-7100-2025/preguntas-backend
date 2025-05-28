package ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.ChangePasswordRequest;
import ucr.ac.cr.learningcommunity.questionservice.events.ChangePasswordEvent;
import ucr.ac.cr.learningcommunity.questionservice.events.Event;
import ucr.ac.cr.learningcommunity.questionservice.events.EventType;
import ucr.ac.cr.learningcommunity.questionservice.events.actions.UpdatePassword;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.ChangePasswordHandler;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.UserRepository;

import java.util.regex.Pattern;

@Service
public class ChangePasswordHandlerImpl implements ChangePasswordHandler {
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Event<?>> kafkaTemplate;


    public ChangePasswordHandlerImpl(UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     KafkaTemplate<String, Event<?>> kafkaTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Result changePassword(String userId, ChangePasswordRequest request) {
        if (request == null) {
            return error(400, "Request cannot be null");
        }
        if (isBlank(request.currentPassword()) || isBlank(request.newPassword())) {
            return error(400, "Both passwords are required");
        }
        if (request.currentPassword().equals(request.newPassword())) {
            return error(400, "New password must be different");
        }
        if (!isPasswordValid(request.newPassword())) {
            return error(400, "Password must be 8+ chars with uppercase, lowercase, number and special char");
        }

        return userRepository.findById(userId)
                .map(user -> {
                    if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
                        return error(401, "Current password is incorrect");
                    }
                    if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
                        return error(400, "Cannot reuse recent passwords");
                    }

                    String newPasswordEncoded = passwordEncoder.encode(request.newPassword());
                    user.setPassword(newPasswordEncoded);
                    userRepository.save(user);
                    ChangePasswordEvent event = new ChangePasswordEvent();
                    event.setEventType(EventType.CHANGEPASSWORD);
                    event.setData(new UpdatePassword(userId, newPasswordEncoded));
                    kafkaTemplate.send("user-password-updated-topic", event);
                    return new Result.Success("Password changed successfully");
                })
                .orElse(error(404, "User not found"));
    }

    private boolean isPasswordValid(String password) {
        return pattern.matcher(password).matches();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private Result error(int code, String msg) {
        return new Result.Error(code, msg);
    }
}
