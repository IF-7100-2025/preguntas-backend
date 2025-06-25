package ucr.ac.cr.learningcommunity.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@Profile("docker")
public class RedisDockerConfig {

    @Bean
    @ConditionalOnMissingBean
    public LettuceConnectionFactory redisConnectionFactory(
            @Value("${spring.redis.host:redis}") String host,
            @Value("${spring.redis.port:6379}") int port) {

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new LettuceConnectionFactory(config);
    }
}