package dev.leocamacho.authservice.handlers.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import ucr.ac.cr.learningcommunity.authservice.events.Event;
import ucr.ac.cr.learningcommunity.authservice.events.EventType;
import ucr.ac.cr.learningcommunity.authservice.events.RegisterUserEvent;
import ucr.ac.cr.learningcommunity.authservice.events.actions.ResgisterUser;
import ucr.ac.cr.learningcommunity.authservice.exceptions.BusinessException;
import ucr.ac.cr.learningcommunity.authservice.exceptions.InvalidInputException;
import ucr.ac.cr.learningcommunity.authservice.handlers.commands.RegisterUserHandler;
import ucr.ac.cr.learningcommunity.authservice.jpa.entities.Role;
import ucr.ac.cr.learningcommunity.authservice.jpa.entities.User;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.RoleRepository;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import ucr.ac.cr.learningcommunity.authservice.redis.RedisService;

import java.util.Optional;
import java.util.Set;

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

    @Mock
    private RedisService redisService;

    @Mock
    private KafkaTemplate<String, Event<?>> kafkaTemplate;

    @InjectMocks
    private RegisterUserHandler registerUserHandler;

    private RegisterUserHandler.Command validCommand;

    @BeforeEach
    public void setup() {
        validCommand = new RegisterUserHandler.Command(
                "randall01@gmail.com",
                "Randall01!",
                "Gamer&678"
        );
    }

    //Happy Path

    @Test
    void testRegisterUserSuccess() {

        Role defaultRole = new Role();
        defaultRole.setName("COLAB");

        when(userRepository.findByUsername("Randall01!")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("randall01@gmail.com")).thenReturn(Optional.empty());

        when(passwordEncoder.encode("Gamer&678")).thenReturn("encodedPassword123");

        when(roleRepository.findByName("COLAB")).thenReturn(Optional.of(defaultRole));

        User savedUser = new User();
        savedUser.setId("1");
        savedUser.setEmail("randall01@gmail.com");
        savedUser.setUsername("Randall01");
        savedUser.setPassword("encodedPassword123");
        savedUser.setRoles(Set.of(defaultRole));

        when(userRepository.save(any())).thenReturn(savedUser);

        registerUserHandler.register(validCommand);

        verify(userRepository).save(any());
        verify(redisService).saveVerificationCode(eq("randall01@gmail.com"), anyString(), eq(10L));
        verify(kafkaTemplate).send(eq("user-registered-topic2"), any(RegisterUserEvent.class));
    }


    @Test
    void testRegisterUserPublishesEventSuccessfully() {

        Role defaultRole = new Role();
        defaultRole.setName("COLAB");

        User user = new User();
        user.setId("1");
        user.setEmail("randall01@gmail.com");
        user.setUsername("Randall01!");
        user.setPassword("encodedPassword123");
        user.setRoles(Set.of(defaultRole));

        when(userRepository.findByUsername("Randall01!")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("randall01@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Gamer&678")).thenReturn("encodedPassword123");
        when(roleRepository.findByName("COLAB")).thenReturn(Optional.of(defaultRole));
        when(userRepository.save(any())).thenReturn(user);
        doNothing().when(redisService).saveVerificationCode(anyString(), anyString(), anyLong());

        ArgumentCaptor<RegisterUserEvent> eventCaptor = ArgumentCaptor.forClass(RegisterUserEvent.class);

        registerUserHandler.register(validCommand);

        verify(redisService).saveVerificationCode(eq("randall01@gmail.com"), anyString(), eq(10L));
        verify(kafkaTemplate).send(eq("user-registered-topic2"), eventCaptor.capture());

        RegisterUserEvent sentEvent = eventCaptor.getValue();
        assertEquals(EventType.NEWUSER, sentEvent.getEventType());

        ResgisterUser data = (ResgisterUser) sentEvent.getData();
        assertEquals("1", data.getUserId());
        assertEquals("Randall01!", data.getUsername());
        assertEquals("randall01@gmail.com", data.getEmail());
        assertEquals("COLAB", data.getRole());
        assertEquals("encodedPassword123", data.getPassword());
        assertNotNull(data.getVerificationCode());
        assertEquals(6, data.getVerificationCode().length());
    }


    //Exceptions

    //RegisterUser

    @Test
    void testUserAlreadyExistsByUsername() {
        when(userRepository.findByUsername("Randall01!")).thenReturn(Optional.of(new ucr.ac.cr.learningcommunity.authservice.jpa.entities.User()));
        BusinessException exception = assertThrows(BusinessException.class, () ->
                registerUserHandler.register(validCommand)
        );
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void testUserAlreadyExistsByEmail() {
        when(userRepository.findByUsername("Randall01!")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("randall01@gmail.com")).thenReturn(Optional.of(new ucr.ac.cr.learningcommunity.authservice.jpa.entities.User()));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                registerUserHandler.register(validCommand)
        );
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void testDefaultRoleNotFound() {
        when(userRepository.findByUsername("Randall01!")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("randall01@gmail.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("COLAB")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                registerUserHandler.register(validCommand)
        );
        assertEquals("Default role not found", exception.getMessage());
    }


    //publishUserRegisteredEvent


}