package ucr.ac.cr.learningcommunity.authservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.authservice.events.Event;
import ucr.ac.cr.learningcommunity.authservice.events.EventType;
import ucr.ac.cr.learningcommunity.authservice.events.actions.ResgisterUser;
import ucr.ac.cr.learningcommunity.authservice.events.actions.UpdatePassword;
import ucr.ac.cr.learningcommunity.authservice.events.actions.UpdateProfile;
import ucr.ac.cr.learningcommunity.authservice.exceptions.BusinessException;
import ucr.ac.cr.learningcommunity.authservice.exceptions.ErrorCodes;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.UserRepository;

@Service

public class KafkaEventsListener {
    private final UserRepository userRepository;

    public KafkaEventsListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "user-password-updated-topic", groupId = "user-sync-group")
    public void handleEvent(Event<?> event) {
        try {
            if (event.getEventType() == EventType.CHANGEPASSWORD) {
                UpdatePassword updatePasswordData = (UpdatePassword) event.getData();
                userRepository.findById(updatePasswordData.getUserId())
                        .ifPresent(user -> {
                            user.setPassword(updatePasswordData.getNewPassword());
                            userRepository.save(user);
                        });
            }
        } catch (Exception e) {
            throw BusinessException.exceptionBuilder()
                    .code(ErrorCodes.ERROR_NOT_IDENTIFIED)
                    .message(e.getMessage())
                    .build();
        }
    }

    @KafkaListener(topics = "user-profile-updated-topic", groupId = "user-sync-group")
    public void handleChangeProfileEvent(Event<?> event) {
        try {
            if (event.getEventType() == EventType.CHANGEPROFILE) {
                UpdateProfile updateProfileData = (UpdateProfile) event.getData();
                userRepository.findById(updateProfileData.getUserId())
                        .ifPresent(user -> {
                            user.setUsername(updateProfileData.getUsername());
                            user.setEmail(updateProfileData.getEmail());
                            userRepository.save(user);
                        });
            }
        } catch (Exception e) {
            throw BusinessException.exceptionBuilder()
                    .code(ErrorCodes.ERROR_NOT_IDENTIFIED)
                    .message(e.getMessage())
                    .build();
        }

    }
}
