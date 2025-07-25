package ucr.ac.cr.learningcommunity.authservice.handlers.queries;

import ucr.ac.cr.learningcommunity.authservice.jpa.entities.User;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.UserRepository;
import ucr.ac.cr.learningcommunity.authservice.models.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static ucr.ac.cr.learningcommunity.authservice.exceptions.BusinessException.exceptionBuilder;

@Service
public class UserAuthenticationQuery {

    @Autowired
    private UserRepository repository;

    public AuthenticatedUser loadUserByUsername(String username) {
        Optional<User> user = repository.findByUsername(username);
        if (user.isPresent()) {
            User u = user.get();
            return new AuthenticatedUser(
                    u.getId(),
                    u.getUsername(),
                    u.getEmail(),
                    u.getPassword(),
                    u.getRoleNames(),
                    u.isVerified()
            );
        } else {
            throw exceptionBuilder().message("User not found with email: " + username).build();
        }
    }

}

