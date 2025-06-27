package ucr.ac.cr.learningcommunity.authservice.handlers.commands;

import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.authservice.jpa.entities.User;
import ucr.ac.cr.learningcommunity.authservice.jpa.repositories.UserRepository;
import ucr.ac.cr.learningcommunity.authservice.redis.RedisService;

import java.util.Optional;

@Service
public class VerificationService {

    private final RedisService redisService;
    private final UserRepository userRepository;

    public VerificationService(RedisService redisService, UserRepository userRepository) {
        this.redisService = redisService;
        this.userRepository = userRepository;
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = redisService.getVerificationCode(email);

        if (storedCode != null && storedCode.equals(code)) {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setVerified(true);
                userRepository.save(user);
            }

            redisService.deleteVerificationCode(email); // opcional
            return true;
        }

        return false;
    }
}
