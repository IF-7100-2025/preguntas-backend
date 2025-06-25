package ucr.ac.cr.learningcommunity.authservice.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DebugController {

    private final LettuceConnectionFactory redisConnectionFactory;
    @Autowired
    private final Environment environment;

    public DebugController(LettuceConnectionFactory redisConnectionFactory, Environment environment) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.environment = environment;
    }

    @GetMapping("/debug/redis")
    public String debugRedis() {
        try {
            redisConnectionFactory.getConnection().ping();
            return "Redis connection OK!";
        } catch (Exception e) {

            return "Redis connection FAILED: " + e.getMessage();
        }
    }
    @GetMapping("/debug/config")
    public Map<String, Object> debugRedisConfig() {
        Map<String, Object> config = new HashMap<>();

        RedisStandaloneConfiguration redisConfig =
                (RedisStandaloneConfiguration) redisConnectionFactory.getStandaloneConfiguration();

        config.put("redisHostConfigured", redisConfig.getHostName());
        config.put("redisPortConfigured", redisConfig.getPort());
        config.put("connectionFactoryClass", redisConnectionFactory.getClass().getName());
        config.put("activeProfiles", Arrays.toString(environment.getActiveProfiles()));

        return config;
    }

}