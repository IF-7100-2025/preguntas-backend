package dev.leocamacho.authservice.handlers.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.authservice.exceptions.BusinessException;
import ucr.ac.cr.learningcommunity.authservice.exceptions.InvalidInputException;
import ucr.ac.cr.learningcommunity.authservice.handlers.commands.RegisterUserHandler;
import ucr.ac.cr.learningcommunity.authservice.jpa.entities.Role;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.RoleRepository;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterUserHandlerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RegisterUserHandler registerUserHandler;

    private RegisterUserHandler.Command validCommand;

    @BeforeEach
    public void setup() {
        validCommand = new RegisterUserHandler.Command(
                "kevin56@gmail.com",
                "Kevin8993!",
                "Gamer&678"
        );
    }

    @Test
    void testRegisterUserSuccess() {
        Role defaultRole = new Role();
        defaultRole.setName("COLAB");

        when(userRepository.findByUsername("Kevin8993!")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("kevin56@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Gamer&678")).thenReturn("142661pswYU");
        when(roleRepository.findByName("COLAB")).thenReturn(Optional.of(defaultRole));

        registerUserHandler.register(validCommand);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testUserAlreadyExistsByUsername() {
        when(userRepository.findByUsername("Kevin8993!")).thenReturn(Optional.of(new ucr.ac.cr.learningcommunity.authservice.jpa.entities.User()));
        BusinessException exception = assertThrows(BusinessException.class, () ->
                registerUserHandler.register(validCommand)
        );
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void testUserAlreadyExistsByEmail() {
        when(userRepository.findByUsername("Kevin8993!")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("kevin56@gmail.com")).thenReturn(Optional.of(new ucr.ac.cr.learningcommunity.authservice.jpa.entities.User()));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                registerUserHandler.register(validCommand)
        );
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void testDefaultRoleNotFound() {
        when(userRepository.findByUsername("Kevin8993!")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("kevin56@gmail.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("COLAB")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                registerUserHandler.register(validCommand)
        );
        assertEquals("Default role not found", exception.getMessage());
    }

    @Test
    void testMissingEmail() {
        RegisterUserHandler.Command invalidCommand = new RegisterUserHandler.Command(null, "kevin", "pass");
        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                registerUserHandler.register(invalidCommand)
        );
        assertEquals("Invalid field email", exception.getMessage());
    }

    @Test
    void testMissingUsername() {
        RegisterUserHandler.Command invalidCommand = new RegisterUserHandler.Command("email@example.com", null, "pass");

        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                registerUserHandler.register(invalidCommand)
        );

        assertEquals("Invalid field username", exception.getMessage());
    }

    @Test
    void testMissingPassword() {
        RegisterUserHandler.Command invalidCommand = new RegisterUserHandler.Command("email@example.com", "user", null);
        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                registerUserHandler.register(invalidCommand)
        );
        assertEquals("Invalid field password", exception.getMessage());
    }


}