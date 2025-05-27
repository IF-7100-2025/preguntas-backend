package ucr.ac.cr.learningcommunity.iaservice.handlers.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.retry.NonTransientAiException;
import ucr.ac.cr.learningcommunity.iaservice.api.types.response.CategoryResponse;
import ucr.ac.cr.learningcommunity.iaservice.handlers.commands.CategorizeQuestionHandler;
import ucr.ac.cr.learningcommunity.iaservice.handlers.commands.impl.CategorizeQuestionHandlerImpl;
import ucr.ac.cr.learningcommunity.iaservice.jpa.entities.CategoryEmbedding;
import ucr.ac.cr.learningcommunity.iaservice.jpa.entities.CategoryEntity;
import ucr.ac.cr.learningcommunity.iaservice.jpa.repositories.CategoryEmbeddingRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategorizeQuestionHandlerTest {

    @Mock
    private OpenAiEmbeddingClient embeddingClient;

    @Mock
    private CategoryEmbeddingRepository repository;

    @Mock
    private CategoryEmbedding categoryEmbedding;

    @InjectMocks
    private CategorizeQuestionHandlerImpl handler;

    @Test
    void testCategorizationIsSuccessful() {

        List<Double> questionEmbedding = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> categoryEmbedding = Arrays.asList(1.0, 2.0, 3.0);

        CategoryEntity category = new CategoryEntity();
        category.setId(1L);
        category.setName("Test Category");

        CategoryEmbedding embedding = new CategoryEmbedding();
        embedding.setCategory(category);
        embedding.setEmbedding(categoryEmbedding);

        when(embeddingClient.embed(anyString())).thenReturn(questionEmbedding);
        when(repository.findAll()).thenReturn(List.of(embedding));

        var result = handler.categorizeQuestion(new CategorizeQuestionHandler.Command("Valid question"));

        assertInstanceOf(CategorizeQuestionHandler.Result.Success.class, result);
        var success = (CategorizeQuestionHandler.Result.Success) result;
        assertEquals(1, success.categories().size());

        CategoryResponse response = success.categories().get(0);
        assertEquals("Test Category", response.name());
        assertEquals(1L, response.id());
    }

    @Test
    void testQuestionIsEmpty() {
        var result = handler.categorizeQuestion(new CategorizeQuestionHandler.Command(""));
        assertInstanceOf(CategorizeQuestionHandler.Result.ValidationError.class, result);
    }

    @Test
    void testQuestionTooShort() {
        var result = handler.categorizeQuestion(new CategorizeQuestionHandler.Command("abc"));
        assertInstanceOf(CategorizeQuestionHandler.Result.ValidationError.class, result);
    }

    @Test
    void textQuestionTooLong() {
        String longText = "a".repeat(201);
        var result = handler.categorizeQuestion(new CategorizeQuestionHandler.Command(longText));
        assertInstanceOf(CategorizeQuestionHandler.Result.ValidationError.class, result);
    }

    @Test
    void testInternalError() {
        when(embeddingClient.embed(anyString())).thenThrow(new RuntimeException("Server error"));

        var result = handler.categorizeQuestion(new CategorizeQuestionHandler.Command("Valid question"));
        assertInstanceOf(CategorizeQuestionHandler.Result.InternalError.class, result);

        var internalError = (CategorizeQuestionHandler.Result.InternalError) result;
        assertTrue(internalError.msg().contains("Server error"));
    }

    @Test
    void testUnauthorized() {
        when(embeddingClient.embed(anyString())).thenThrow(new NonTransientAiException("Unauthorized"));

        var result = handler.categorizeQuestion(new CategorizeQuestionHandler.Command("Valid question"));
        assertInstanceOf(CategorizeQuestionHandler.Result.InternalError.class, result);

        var internalError = (CategorizeQuestionHandler.Result.InternalError) result;
        assertTrue(internalError.msg().contains("Unauthorized"));
    }




}
