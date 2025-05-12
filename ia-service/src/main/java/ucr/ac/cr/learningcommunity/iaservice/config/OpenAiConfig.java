package ucr.ac.cr.learningcommunity.iaservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.api.OpenAiApi;

@Configuration
public class OpenAiConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Bean
    public OpenAiEmbeddingClient openAiEmbeddingClient() {
        OpenAiApi openAiApi = new OpenAiApi(apiKey);
        return new OpenAiEmbeddingClient(openAiApi);
    }
}
