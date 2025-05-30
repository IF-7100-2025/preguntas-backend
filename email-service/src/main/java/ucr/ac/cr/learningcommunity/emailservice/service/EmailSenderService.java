package ucr.ac.cr.learningcommunity.emailservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.emailservice.template.EmailTemplateService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailSenderService {

    @Value("${spring.mail.from}")
    private String fromEmail;

    private final JavaMailSender mailSender;
    private final EmailTemplateService emailTemplateService;

    public EmailSenderService(JavaMailSender mailSender, EmailTemplateService emailTemplateService) {
        this.mailSender = mailSender;
        this.emailTemplateService = emailTemplateService;
    }

    public void sendWelcomeEmail(String email, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("¡Bienvenido a Conti Learning Community!");
            helper.setText(emailTemplateService.createWelcomeTemplate(username), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}