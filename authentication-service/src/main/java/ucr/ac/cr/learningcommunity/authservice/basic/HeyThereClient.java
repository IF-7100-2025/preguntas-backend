package ucr.ac.cr.learningcommunity.authservice.basic;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("basic-service")
public interface HeyThereClient {
    @RequestMapping("/api/private/basic")
    String greeting();
}