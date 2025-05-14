package ucr.ac.cr.learningcommunity.authservice.handlers.query.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.authservice.api.types.LoginRequest;
import ucr.ac.cr.learningcommunity.authservice.api.types.LoginResponse;
import ucr.ac.cr.learningcommunity.authservice.handlers.query.LoginUserQuery;
import ucr.ac.cr.learningcommunity.authservice.jpa.entities.User;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.UserRepository;

import java.util.Optional;

@Service
public class LoginUserQueryImpl implements LoginUserQuery {

    private final UserRepository userRepository;

    @Autowired
    public LoginUserQueryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Result query(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.username());

        if (optionalUser.isEmpty()) {
            return new Result.UserNotFound();
        }

        User user = optionalUser.get();

        // Suponiendo que la contraseña está en texto plano por ahora (NO recomendado para prod)
        if (!user.getPassword().equals(request.password())) {
            return new Result.InvalidCredentials();
        }

        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
        );

        return new Result.Success(response);
    }
}