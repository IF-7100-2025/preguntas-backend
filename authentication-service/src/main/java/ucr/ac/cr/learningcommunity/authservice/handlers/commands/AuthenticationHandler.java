package ucr.ac.cr.learningcommunity.authservice.handlers.commands;

import ucr.ac.cr.learningcommunity.authservice.exceptions.BusinessException;
import ucr.ac.cr.learningcommunity.authservice.handlers.queries.UserAuthenticationQuery;
import ucr.ac.cr.learningcommunity.authservice.http.JwtService;
import ucr.ac.cr.learningcommunity.authservice.models.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationHandler {

    @Autowired
    private UserAuthenticationQuery userAuthenticationQuery;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String authenticateWithJwt(String username, String password) {
        AuthenticatedUser authenticated = authenticate(username, password);
        return jwtService.generateToken(authenticated);
    }

    private AuthenticatedUser authenticate(String username, String password) {
        var user = userAuthenticationQuery.loadUserByUsername(username);
        if (user == null)
            throw new BusinessException("User not provided");
        if (user.password() == null)
            throw new BusinessException("Password not provided");

        /*
        //TODO Compara el password encriptado
        if (!password.equals(user.password()))
            throw new BusinessException("Invalid credentials");
        */
        if (!passwordEncoder.matches(password, user.password()))
            throw new BusinessException("Invalid credentials");

        return user;
    }
}
