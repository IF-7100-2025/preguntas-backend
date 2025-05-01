package ucr.ac.cr.learningcommunity.iaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class IAserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IAserviceApplication.class, args);
	}

}
