package ucr.ac.cr.learningcommunity.authservice.api;


import ucr.ac.cr.learningcommunity.authservice.api.types.AuthenticationRequest;
import ucr.ac.cr.learningcommunity.authservice.api.types.AuthenticationResponse;
import ucr.ac.cr.learningcommunity.authservice.handlers.commands.AuthenticationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class AuthenticationController {

    @Autowired
    private AuthenticationHandler handler;

    @PostMapping(value = "/api/public/auth/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest authenticationRequest) {
        String token = handler.authenticateWithJwt(authenticationRequest.username(), authenticationRequest.password());
        return new AuthenticationResponse(token);
    }

}
