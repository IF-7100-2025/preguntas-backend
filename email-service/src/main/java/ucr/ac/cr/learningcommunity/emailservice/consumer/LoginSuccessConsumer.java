package ucr.ac.cr.learningcommunity.emailservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ucr.ac.cr.learningcommunity.emailservice.events.LoginSuccessEvent;
import ucr.ac.cr.learningcommunity.emailservice.events.actions.LoginSuccess;
import ucr.ac.cr.learningcommunity.emailservice.service.EmailSenderService;

@Component
public class LoginSuccessConsumer {

    private final EmailSenderService emailSenderService;

    public LoginSuccessConsumer(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @KafkaListener(topics = "user-login-success-topic2")
    public void handleLoginSuccess(LoginSuccessEvent event) {
        LoginSuccess loginData = (LoginSuccess)  event.getData();
        emailSenderService.sendWelcomeEmail(
                loginData.getEmail(),
                loginData.getUsername()
        );
    }
}
