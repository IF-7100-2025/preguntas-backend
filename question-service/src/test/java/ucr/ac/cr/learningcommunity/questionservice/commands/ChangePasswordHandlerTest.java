package ucr.ac.cr.learningcommunity.questionservice.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.ChangePasswordRequest;
import ucr.ac.cr.learningcommunity.questionservice.events.ChangePasswordEvent;
import ucr.ac.cr.learningcommunity.questionservice.events.Event;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.ChangePasswordHandler.Result;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl.ChangePasswordHandlerImpl;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChangePasswordHandlerTest {

    @Mock private UserRepository userRepository;
    @Mock private KafkaTemplate<String, Event<?>> kafkaTemplate;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ChangePasswordHandlerImpl changePasswordHandler;

    private String userId;
    private String currentPassword;
    private String newPassword;
    private String encodedOldPassword;
    private String encodedNewPassword;

    @BeforeEach
    void setUp() {
        userId = "b45b2c57-03b4-4970-9021-ccf5dcb6dabb";
        currentPassword = "Secure1$Pass";
        newPassword = "Better2@Word";
        encodedOldPassword = "$2a$10$7Q5Avy49XyBtKy6qA5/6AeR77JS1FvZcAKR.8Mr1mLg0IYhTOdtDe";
        encodedNewPassword = "$2a$10$TSzqIsU2YYvX1mV7jZrCquOXPHccApT7FuhqlVL9RCUZkNfX2Fe3K";
    }

    @Test
    void testChangePasswordSuccess() {
        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setPassword(encodedOldPassword);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, encodedOldPassword)).thenReturn(true);
        when(passwordEncoder.matches(newPassword, encodedOldPassword)).thenReturn(false);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
        Result result = changePasswordHandler.changePassword(userId, request);

        assertInstanceOf(Result.Success.class, result);
        assertEquals("Password changed successfully", ((Result.Success) result).message());
        verify(userRepository).save(user);
        verify(kafkaTemplate).send(eq("user-password-updated-topic"), any(ChangePasswordEvent.class));
        assertEquals(encodedNewPassword, user.getPassword());
    }

    @Test
    void testChangePassword_UserNotFound_ShouldReturnError() {
        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Result result = changePasswordHandler.changePassword(userId, request);

        assertInstanceOf(Result.Error.class, result);
        Result.Error error = (Result.Error) result;
        assertEquals("User not found", error.message());
    }

    @Test
    void testChangePassword_InvalidCurrentPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setPassword(encodedOldPassword);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, encodedOldPassword)).thenReturn(false);

        Result result = changePasswordHandler.changePassword(userId, request);

        assertInstanceOf(Result.Error.class, result);
        Result.Error error = (Result.Error) result;
        assertEquals("Current password is incorrect", error.message());
    }

}