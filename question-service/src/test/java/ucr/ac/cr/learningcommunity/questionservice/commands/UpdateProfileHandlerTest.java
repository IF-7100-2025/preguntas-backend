package ucr.ac.cr.learningcommunity.questionservice.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.UpdateProfileRequest;
import ucr.ac.cr.learningcommunity.questionservice.events.Event;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.UpdateProfileHandler;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl.UpdateProfileHandlerImpl;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProfileHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaTemplate<String, Event<?>> kafkaTemplate;

    @InjectMocks
    private UpdateProfileHandlerImpl updateProfileHandler;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId("b45b2c57-03b4-4970-9021-ccf5dcb6ddff");
        user.setUsername("sofia.mendez");
        user.setEmail("sofia.mendez@gmail.com");
        user.setProfileImage("data:image/png;base64,iVBORw0KGgoAAAANSUhEUg==");
    }

    @Test
    void updateProfileSuccessfully() {
        UpdateProfileRequest request = new UpdateProfileRequest(
                "sofia.mendez23",
                "sofiam23@gmail.com",
                "data:image/png;base64,newimagebase64=="
        );

        when(userRepository.findById("b45b2c57-03b4-4970-9021-ccf5dcb6ddff")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("sofia.mendez23")).thenReturn(false);
        when(userRepository.existsByEmail("sofiam23@gmail.com")).thenReturn(false);

        UpdateProfileHandler.Result result = updateProfileHandler.updateProfile("b45b2c57-03b4-4970-9021-ccf5dcb6ddff", request);

        assertInstanceOf(UpdateProfileHandler.Result.Success.class, result);
        assertEquals("Profile updated successfully", ((UpdateProfileHandler.Result.Success) result).message());

        verify(userRepository).save(user);
        verify(kafkaTemplate).send(eq("user-profile-updated-topic"), any());
    }

    @Test
    void updateProfileFailsWhenEmailAlreadyExists() {
        UpdateProfileRequest request = new UpdateProfileRequest(
                "sofia.mendez23",
                "daniela.mendez@gmail.com",
                null
        );

        when(userRepository.findById("b45b2c57-03b4-4970-9021-ccf5dcb6ddff")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("daniela.mendez@gmail.com")).thenReturn(true);

        UpdateProfileHandler.Result result = updateProfileHandler.updateProfile("b45b2c57-03b4-4970-9021-ccf5dcb6ddff", request);

        assertInstanceOf(UpdateProfileHandler.Result.Error.class, result);
        assertEquals(409, ((UpdateProfileHandler.Result.Error) result).status());
        assertEquals("Email already in use", ((UpdateProfileHandler.Result.Error) result).message());

        verify(userRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    void updateProfileFailsWhenUserNotFound() {
        UpdateProfileRequest request = new UpdateProfileRequest(
                "andres.gomez",
                "andres.gomez@outlook.com",
                null
        );

        when(userRepository.findById("b45b2c57-03b4-4970-9021-ccf5dcb6dd00")).thenReturn(Optional.empty());

        UpdateProfileHandler.Result result = updateProfileHandler.updateProfile("b45b2c57-03b4-4970-9021-ccf5dcb6dd00", request);

        assertInstanceOf(UpdateProfileHandler.Result.Error.class, result);
        assertEquals(404, ((UpdateProfileHandler.Result.Error) result).status());
        assertTrue(((UpdateProfileHandler.Result.Error) result).message().toLowerCase().contains("not found"));

        verify(userRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(any(), any());
    }
}