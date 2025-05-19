package dev.leocamacho.authservice.integration.commands;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ucr.ac.cr.learningcommunity.authservice.ClientServiceApplication;
import ucr.ac.cr.learningcommunity.authservice.handlers.commands.AuthenticationHandler;
import ucr.ac.cr.learningcommunity.authservice.http.JwtService;
import ucr.ac.cr.learningcommunity.authservice.jpa.entities.Role;
import ucr.ac.cr.learningcommunity.authservice.jpa.entities.User;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.RoleRepository;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootTest(classes = ClientServiceApplication.class)
@Transactional
public class AuthenticationHandlerIntegrationTest {

    @Autowired
    private AuthenticationHandler authenticationHandler;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testAuthenticateWithJwt() {
        User user = new User();
        user.setUsername("Aaron95");
        user.setEmail("aaronrs@gmail.com");
        user.setPassword(passwordEncoder.encode("inge8800!"));

        Role role = roleRepository.findByName("COLAB").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("COLAB");
            return roleRepository.save(newRole);
        });

        user.setRoles(Set.of(role));
        userRepository.save(user);

        String token = authenticationHandler.authenticateWithJwt("Aaron95", "inge8800!");

        assertNotNull(token);
        assertTrue(jwtService.isValidToken(token));
        assertEquals("aaronrs@gmail.com", jwtService.getUsernameFromToken(token));
    }
}