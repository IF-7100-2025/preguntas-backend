package dev.leocamacho.authservice.integration.commands;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ucr.ac.cr.learningcommunity.authservice.ClientServiceApplication;
import ucr.ac.cr.learningcommunity.authservice.handlers.commands.RegisterUserHandler;
import ucr.ac.cr.learningcommunity.authservice.jpa.entities.User;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.UserRepository;


@SpringBootTest(classes = ClientServiceApplication.class)
@Transactional
public class RegisterUserHandlerIntegrationTest {
    @Autowired
    private RegisterUserHandler registerUserHandler;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testRegisterUserSuccessfully() {
        String email = "ana78@outlook.com";
        String username = "Ana70*";
        String password = "BooksLover8";

        RegisterUserHandler.Command command =
                new RegisterUserHandler.Command(email, username, password);

        registerUserHandler.register(command);

        User user = userRepository.findByEmail(email).orElse(null);

        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertNotNull(user.getPassword());
        assertFalse(user.getPassword().equals(password));
        assertTrue(user.getRoles().stream().anyMatch(role -> role.getName().equals("COLAB")));
    }
}
