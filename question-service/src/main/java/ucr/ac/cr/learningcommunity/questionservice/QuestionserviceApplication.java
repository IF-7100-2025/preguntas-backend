package ucr.ac.cr.learningcommunity.questionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class QuestionserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuestionserviceApplication.class, args);
	}

}
