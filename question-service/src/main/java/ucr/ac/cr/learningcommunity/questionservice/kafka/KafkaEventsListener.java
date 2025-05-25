package ucr.ac.cr.learningcommunity.questionservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import ucr.ac.cr.learningcommunity.questionservice.events.Event;
import ucr.ac.cr.learningcommunity.questionservice.events.EventType;
import ucr.ac.cr.learningcommunity.questionservice.events.RegisterUserEvent;
import ucr.ac.cr.learningcommunity.questionservice.events.actions.ResgisterUser;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.UserRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

@Service
public class KafkaEventsListener {
    private final UserRepository userRepository;

    public KafkaEventsListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "user-registered-topic2", groupId = "user-sync-group")
    public void handleEvent(Event<?> event) {
        try {
            if (event.getEventType() == EventType.NEWUSER) {
                ResgisterUser registerUserData = (ResgisterUser) event.getData();
                if (userRepository.findById(registerUserData.getUserId()).isEmpty()) {
                    UserEntity user = new UserEntity();
                    user.setId(registerUserData.getUserId());
                    user.setUsername(registerUserData.getUsername());
                    user.setEmail(registerUserData.getEmail());
                    user.setRole(registerUserData.getRole());
                    user.setPassword(registerUserData.getPassword());
                    userRepository.save(user);
                }
            }
        } catch (Exception e) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.ERROR_NOT_IDENTIFIED)
                    .message(ErrorCode.ERROR_NOT_IDENTIFIED.getDefaultMessage())
                    .build();
        }
    }
//    @KafkaListener(topics = "user-password-updated-topic", groupId = "user-sync-group")
//    public void handleEvent2(Event<?> event) {
//        try {
//            if (event.getEventType() == EventType.CHANGEPASSWORD) {
//                ResgisterUser registerUserData = (ResgisterUser) event.getData();
//                if (userRepository.findById(registerUserData.getUserId()).isEmpty()) {
//                    UserEntity user = new UserEntity();
//                    user.setId(registerUserData.getUserId());
//                    user.setUsername(registerUserData.getUsername());
//                    user.setEmail(registerUserData.getEmail());
//                    user.setRole(registerUserData.getRole());
//                    user.setPassword(registerUserData.getPassword());
//                    userRepository.save(user);
//                }
//            }
//        } catch (Exception e) {
//            throw BaseException.exceptionBuilder()
//                    .code(ErrorCode.ERROR_NOT_IDENTIFIED)
//                    .message(ErrorCode.ERROR_NOT_IDENTIFIED.getDefaultMessage())
//                    .build();
//        }
//    }
}
