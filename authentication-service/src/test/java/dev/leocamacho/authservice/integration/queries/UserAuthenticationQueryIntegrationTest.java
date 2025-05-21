package dev.leocamacho.authservice.integration.queries;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

import ucr.ac.cr.learningcommunity.authservice.ClientServiceApplication;
import ucr.ac.cr.learningcommunity.authservice.handlers.queries.UserAuthenticationQuery;
import ucr.ac.cr.learningcommunity.authservice.http.JwtService;
import ucr.ac.cr.learningcommunity.authservice.jpa.entities.Role;
import ucr.ac.cr.learningcommunity.authservice.jpa.entities.User;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.RoleRepository;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.UserRepository;
import ucr.ac.cr.learningcommunity.authservice.models.AuthenticatedUser;

import java.util.Set;

@SpringBootTest(classes = ClientServiceApplication.class)
@Transactional
public class UserAuthenticationQueryIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserAuthenticationQuery userAuthenticationQuery;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testLoadUserByUsernameSuccessfully() {

        Role role = roleRepository.findByName("COLAB").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("COLAB");
            return roleRepository.save(newRole);
        });

        User user = new User();
        user.setUsername("Jose89");
        user.setEmail("joserv10@gmail.com");
        user.setPassword(passwordEncoder.encode("Techno2025"));
        user.setRoles(Set.of(role));
        userRepository.save(user);

        AuthenticatedUser authenticatedUser = userAuthenticationQuery.loadUserByUsername("Jose89");

        assertNotNull(authenticatedUser);
        assertEquals(user.getId(), authenticatedUser.id());
        assertEquals(user.getEmail(), authenticatedUser.username());
        assertTrue(authenticatedUser.authorities().contains("COLAB"));
    }
}
