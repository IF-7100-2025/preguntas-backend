package ucr.ac.cr.learningcommunity.questionservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.questionservice.events.UserRegisteredEvent;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.User;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.UserRepository;

@Service
public class KafkaConfig {
    private final UserRepository userRepository;

    public KafkaConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "user-registered-topic", groupId = "user-sync-group")
    public void consume(UserRegisteredEvent event) {
        if (userRepository.findById(event.getUserId()).isEmpty()) {
            User user = new User();
            user.setId(event.getUserId());
            user.setUsername(event.getUsername());
            user.setEmail(event.getEmail());
            user.setRole(event.getRole());
            user.setPassword(event.getPassword());

            userRepository.save(user);
        }
    }
}