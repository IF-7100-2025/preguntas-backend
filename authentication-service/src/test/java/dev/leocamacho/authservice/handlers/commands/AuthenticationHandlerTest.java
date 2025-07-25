package dev.leocamacho.authservice.handlers.commands;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import ucr.ac.cr.learningcommunity.authservice.events.Event;
import ucr.ac.cr.learningcommunity.authservice.events.EventType;
import ucr.ac.cr.learningcommunity.authservice.events.LoginSuccessEvent;
import ucr.ac.cr.learningcommunity.authservice.events.actions.LoginSuccess;
import ucr.ac.cr.learningcommunity.authservice.exceptions.BusinessException;
import ucr.ac.cr.learningcommunity.authservice.handlers.commands.AuthenticationHandler;
import ucr.ac.cr.learningcommunity.authservice.handlers.queries.UserAuthenticationQuery;
import ucr.ac.cr.learningcommunity.authservice.http.JwtService;
import ucr.ac.cr.learningcommunity.authservice.models.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationHandlerTest {
    @Mock
    private UserAuthenticationQuery userAuthenticationQuery;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private KafkaTemplate<String, Event<?>> kafkaTemplate;

    @InjectMocks
    private AuthenticationHandler authenticationHandler;

    //HappyPath

    @Test
    void testAuthenticationSuccess() {
        String username = "Andrés95";
        String email = "andres95@gmail.com";
        String password = "pass001*";
        String encodedPassword = "password95";
        String jwtToken = "mock.jwt.token";

        AuthenticatedUser mockUser = new AuthenticatedUser("1", username, email, encodedPassword, Set.of("USER"), true);
        when(userAuthenticationQuery.loadUserByUsername(username)).thenReturn(mockUser);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtService.generateToken(mockUser)).thenReturn(jwtToken);
        String token = authenticationHandler.authenticateWithJwt(username, password);
        assertEquals(jwtToken, token);

        verify(kafkaTemplate, times(1)).send(eq("user-login-success-topic3"), argThat(event ->
                event instanceof LoginSuccessEvent &&
                        event.getEventType() == EventType.LOGIN_SUCCESS &&
                        ((LoginSuccess) event.getData()).getEmail().equals(email) &&
                        ((LoginSuccess) event.getData()).getUsername().equals(username)
        ));

    }

    //Exceptions

    @Test
    void testIncorrectPassword() {
        String username = "Karla001";
        String email = "karla08@gmail.com";
        String password = "Sailor917$";
        String encodedPassword = "123passSl";
        AuthenticatedUser mockUser = new AuthenticatedUser("1", username, email, encodedPassword, Set.of("USER"), true);
        when(userAuthenticationQuery.loadUserByUsername(username)).thenReturn(mockUser);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);
        Exception exception = assertThrows(BusinessException.class, () ->
                authenticationHandler.authenticateWithJwt(username, password)
        );
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void testUserNotFound() {
        String username = "Paolo2201";
        String password = "Sports13!";
        when(userAuthenticationQuery.loadUserByUsername(username))
                .thenThrow(new BusinessException("User not found with email: " + username));
        BusinessException exception = assertThrows(BusinessException.class, () ->
                authenticationHandler.authenticateWithJwt(username, password)
        );
        assertEquals("User not found with email: " + username, exception.getMessage());
    }

    @Test
    void testMissingUsername() {
        String username = "";
        String password = "passW0818#";
        Exception exception = assertThrows(BusinessException.class, () ->
                authenticationHandler.authenticateWithJwt(username, password)
        );
        assertEquals("Username not provided", exception.getMessage());
    }

    @Test
    void testMissingPassword() {
        String username = "Daniel8610&";
        String password = "";

        Exception exception = assertThrows(BusinessException.class, () ->
                authenticationHandler.authenticateWithJwt(username, password)
        );
        assertEquals("Password not provided", exception.getMessage());
    }
}
