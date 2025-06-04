package ucr.ac.cr.learningcommunity.emailservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ucr.ac.cr.learningcommunity.emailservice.events.RegisterUserEvent;
import ucr.ac.cr.learningcommunity.emailservice.events.actions.ResgisterUser;
import ucr.ac.cr.learningcommunity.emailservice.service.EmailSenderService;

@Component
public class UserRegisteredConsumer {

    private final EmailSenderService emailService;

    public UserRegisteredConsumer(EmailSenderService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-registered-topic2")
    public void handleUserRegistration(RegisterUserEvent event) {
        ResgisterUser user = (ResgisterUser) event.getData();

        emailService.sendVerificationEmail(
                user.getEmail(),
                user.getUsername(),
                user.getVerificationCode()
        );
    }
}
