package ucr.ac.cr.learningcommunity.authservice.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ucr.ac.cr.learningcommunity.authservice.api.types.VerificationRequest;
import ucr.ac.cr.learningcommunity.authservice.handlers.commands.VerificationService;

@RestController
@RequestMapping("/api/public/auth")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody VerificationRequest request) {
        boolean success = verificationService.verifyCode(request.email(), request.code());

        if (success) {
            return ResponseEntity.ok("Código verificado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código incorrecto o expirado");
        }
    }

}
