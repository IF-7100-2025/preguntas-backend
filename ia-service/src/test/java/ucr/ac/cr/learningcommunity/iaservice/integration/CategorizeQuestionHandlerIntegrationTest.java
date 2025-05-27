package ucr.ac.cr.learningcommunity.iaservice.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.learningcommunity.iaservice.api.types.response.CategoryResponse;
import ucr.ac.cr.learningcommunity.iaservice.handlers.commands.CategorizeQuestionHandler;
import ucr.ac.cr.learningcommunity.iaservice.jpa.entities.CategoryEmbedding;
import ucr.ac.cr.learningcommunity.iaservice.jpa.entities.CategoryEntity;
import ucr.ac.cr.learningcommunity.iaservice.jpa.repositories.CategoryEmbeddingRepository;
import ucr.ac.cr.learningcommunity.iaservice.jpa.repositories.CategoryRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class CategorizeQuestionHandlerIntegrationTest {

    @Autowired
    private CategorizeQuestionHandler handler;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryEmbeddingRepository repository;



    @TestConfiguration
    static class TestConfig {
        @Bean
        public OpenAiEmbeddingClient embeddingClient() {
            OpenAiEmbeddingClient mockClient = mock(OpenAiEmbeddingClient.class);
            when(mockClient.embed(anyString())).thenReturn(Arrays.asList(1.0, 2.0, 3.0));
            return mockClient;
        }
    }

    @Test
    void testCategoryMatchesEmbedding() {

        List<Double> embeddingVector = Arrays.asList(1.0, 2.0, 3.0);

        CategoryEntity category = new CategoryEntity();
        category.setName("Programación Java");
        category = categoryRepository.save(category);

        CategoryEmbedding categoryEmbedding = new CategoryEmbedding(category, embeddingVector);
        repository.save(categoryEmbedding);

        var result = handler.categorizeQuestion(new CategorizeQuestionHandler.Command("Cuál es un patrón de aruqitectura común en Java?"));

        assertInstanceOf(CategorizeQuestionHandler.Result.Success.class, result);

        var success = (CategorizeQuestionHandler.Result.Success) result;
        assertEquals(1, success.categories().size());

        CategoryResponse response = success.categories().get(0);
        assertEquals("Programación Java", response.name());
    }
}