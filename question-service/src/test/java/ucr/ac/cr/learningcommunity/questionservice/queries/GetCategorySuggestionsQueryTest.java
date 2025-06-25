package ucr.ac.cr.learningcommunity.questionservice.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.questionservice.api.rests.IAIntegrationClient;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.CategoryResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetCategorySuggestionsQuery;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl.GetCategorySuggestionsQueryImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetCategorySuggestionsQueryTest {

    @Mock
    private IAIntegrationClient iaIntegrationClient;

    @InjectMocks
    private GetCategorySuggestionsQueryImpl getCategorySuggestionsQuery;

    private List<CategoryResponse> mockSuggestions;

    @BeforeEach
    void setUp() {
        mockSuggestions = List.of(
                new CategoryResponse(1L, "Geografía"),
                new CategoryResponse(2L, "Historia")
        );
    }

    @Test
    void testGetCategorySuggestionsSuccess() {
        String question = "Cuál es la capital de Francia?";
        when(iaIntegrationClient.getCategorySuggestionsFromIA(question)).thenReturn(mockSuggestions);

        GetCategorySuggestionsQuery.Result result = getCategorySuggestionsQuery.getCategorySuggestions(question);

        assertInstanceOf(GetCategorySuggestionsQuery.Result.Success.class, result);
        List<CategoryResponse> categories = ((GetCategorySuggestionsQuery.Result.Success) result).categories();
        assertEquals(2, categories.size());
        assertEquals("Geografía", categories.get(0).name());
    }

    @Test
    void testGetCategoryIAException() {
        String question = "¿Cuál es la capital de Francia?";
        when(iaIntegrationClient.getCategorySuggestionsFromIA(question))
                .thenThrow(new RuntimeException("IA service error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            getCategorySuggestionsQuery.getCategorySuggestions(question);
        });

        assertEquals("Error with IA service, please try again later", exception.getMessage());
    }

}