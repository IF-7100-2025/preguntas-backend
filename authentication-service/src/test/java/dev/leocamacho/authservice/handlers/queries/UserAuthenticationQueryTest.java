package dev.leocamacho.authservice.handlers.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.authservice.exceptions.BusinessException;
import ucr.ac.cr.learningcommunity.authservice.handlers.queries.UserAuthenticationQuery;
import ucr.ac.cr.learningcommunity.authservice.jpa.entities.Role;
import ucr.ac.cr.learningcommunity.authservice.jpa.entities.User;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.UserRepository;
import ucr.ac.cr.learningcommunity.authservice.models.AuthenticatedUser;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAuthenticationQueryTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAuthenticationQuery userAuthenticationQuery;

    private User user;

    @BeforeEach
    public void setup() {
        Role role = new Role();
        role.setName("Admin");

        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("Sebas0012");
        user.setEmail("sebas62@outlook.com");
        user.setPassword("movies25*");
        user.setRoles(Set.of(role));
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        AuthenticatedUser result = userAuthenticationQuery.loadUserByUsername(user.getUsername());
        assertNotNull(result);
        assertEquals(user.getId(), result.id());
        assertEquals(user.getEmail(), result.username());
        assertEquals(user.getPassword(), result.password());
        assertTrue(result.authorities().contains("Admin"));
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        String username = "Karla001";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        BusinessException exception = assertThrows(BusinessException.class, () ->
                userAuthenticationQuery.loadUserByUsername(username)
        );
        assertEquals("User not found with email: " + username, exception.getMessage());
    }
}
