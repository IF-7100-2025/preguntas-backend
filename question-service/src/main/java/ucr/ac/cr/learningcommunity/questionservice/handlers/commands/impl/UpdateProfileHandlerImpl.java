package ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.UpdateProfileRequest;
import ucr.ac.cr.learningcommunity.questionservice.events.Event;
import ucr.ac.cr.learningcommunity.questionservice.events.EventType;
import ucr.ac.cr.learningcommunity.questionservice.events.UpdateProfileEvent;
import ucr.ac.cr.learningcommunity.questionservice.events.actions.UpdateProfile;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.UpdateProfileHandler;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.UserRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.util.regex.Pattern;

@Service
@Transactional
public class UpdateProfileHandlerImpl implements UpdateProfileHandler {
    private final UserRepository userRepository;
    private static final Pattern BASE64_PATTERN = Pattern.compile("^data:image/(png|jpg|jpeg);base64,[a-zA-Z0-9+/]+={0,2}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private final KafkaTemplate<String, Event<?>> kafkaTemplate;

    public UpdateProfileHandlerImpl(UserRepository userRepository, KafkaTemplate<String, Event<?>> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Result updateProfile(String userId, UpdateProfileRequest request) {
        if (request == null) return error(400, "Update request cannot be null");
        if (isBlank(request.username())) return error(400, "Username cannot be empty");
        if (isBlank(request.email()) || !isValidEmail(request.email())) {
            return error(400, "Invalid email format");
        }
        if (request.email().length()>100 || request.username().length()>50) {
            return error(400, "Username or email is too long");
        }
        if (request.profileImage() != null && !isValidBase64Image(request.profileImage())) {
            return error(400, "Invalid image format. Must be base64 encoded PNG, JPG or JPEG");
        }

        return userRepository.findById(userId)
                .map(user -> {
                    boolean changingEmail = !user.getEmail().equals(request.email());
                    boolean changingUsername = !user.getUsername().equals(request.username());
                    if (changingEmail && userRepository.existsByEmail(request.email())) {
                        return error(409, "Email already in use");
                    }
                    if (changingUsername && userRepository.existsByUsername(request.username())) {
                        return error(409, "Username already taken");
                    }
                    boolean updated = false;
                    if (changingEmail) {
                        user.setEmail(request.email());
                        updated = true;
                    }
                    if (changingUsername) {
                        user.setUsername(request.username());
                        updated = true;
                    }
                    if (request.profileImage() != null && !request.profileImage().isBlank()) {
                        user.setProfileImage(request.profileImage());
                        updated = true;
                    }

                    if (updated) {
                        userRepository.save(user);
                        UpdateProfile updateData = new UpdateProfile(
                                userId,
                                changingUsername ? request.username() : user.getUsername(),
                                changingEmail ? request.email() : user.getEmail());
                        UpdateProfileEvent event = new UpdateProfileEvent();
                        event.setEventType(EventType.CHANGEPROFILE);
                        event.setData(updateData);
                        kafkaTemplate.send("user-profile-updated-topic", event);
                    }
                    return new Result.Success("Profile updated successfully");
                })
                .orElse(error(ErrorCode.USER_NOT_FOUND.getHttpStatus(),
                        ErrorCode.USER_NOT_FOUND.getDefaultMessage()));
    }

    private boolean isValidBase64Image(String image) {
        return BASE64_PATTERN.matcher(image).matches();
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private Result error(int code, String msg) {
        return new Result.Error(code, msg);
    }
}