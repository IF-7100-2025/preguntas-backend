package ucr.ac.cr.learningcommunity.authservice.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveVerificationCode(String email, String code, long minutesToExpire) {
        String key = "verification:" + email;
        redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(minutesToExpire));
    }

    public String getVerificationCode(String email) {
        return redisTemplate.opsForValue().get("verification:" + email);
    }

    public void deleteVerificationCode(String email) {
        redisTemplate.delete("verification:" + email);
    }
}
