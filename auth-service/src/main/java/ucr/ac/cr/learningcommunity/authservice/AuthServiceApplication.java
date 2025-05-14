package ucr.ac.cr.learningcommunity.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(AuthServiceApplication.class);
        application.setAddCommandLineProperties(false);
        application.run(args);
    }

}
