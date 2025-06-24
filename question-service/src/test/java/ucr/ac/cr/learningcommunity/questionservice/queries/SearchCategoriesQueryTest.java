package ucr.ac.cr.learningcommunity.questionservice.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.RandomCategoriesResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.SearchCategoriesQuery;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl.SearchCategoriesQueryImpl;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.CategoryEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.CategoryRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchCategoriesQueryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private SearchCategoriesQueryImpl searchCategoriesQuery;

    @Test
    public void testSearch_WithValidTerm_ReturnsSuccess() {

        String term = "Matemáticas";

        CategoryEntity category = new CategoryEntity();
        category.setId(1L);
        category.setName("Matemáticas");

        when(categoryRepository.findByNameContainingIgnoreCase(term))
                .thenAnswer(invocation -> {
                    return List.of(category);
                });

        when(questionRepository.countByCategories_Id(1L))
                .thenReturn(3);

        SearchCategoriesQuery.Result result = searchCategoriesQuery.search(term);

        assertInstanceOf(SearchCategoriesQuery.Result.Success.class, result);
        List<RandomCategoriesResponse> responseList = ((SearchCategoriesQuery.Result.Success) result).categories();

        assertEquals(1, responseList.size());
        assertEquals("Matemáticas", responseList.get(0).name());
        assertEquals(3, responseList.get(0).questions());
    }

    @Test
    public void testSearch_WithNullOrEmptyTerm_ReturnsEmptyList() {
        SearchCategoriesQuery.Result resultNull = searchCategoriesQuery.search(null);
        SearchCategoriesQuery.Result resultEmpty = searchCategoriesQuery.search("  ");

        assertInstanceOf(SearchCategoriesQuery.Result.Success.class, resultNull);
        assertInstanceOf(SearchCategoriesQuery.Result.Success.class, resultEmpty);

        List<RandomCategoriesResponse> listNull = ((SearchCategoriesQuery.Result.Success) resultNull).categories();
        List<RandomCategoriesResponse> listEmpty = ((SearchCategoriesQuery.Result.Success) resultEmpty).categories();

        assertNotNull(listNull);
        assertTrue(listNull.isEmpty());

        assertNotNull(listEmpty);
        assertTrue(listEmpty.isEmpty());
    }

    @Test
    public void testSearchReturnsError() {
        String term = "ErrorTerm";

        when(categoryRepository.findByNameContainingIgnoreCase(term))
                .thenThrow(new RuntimeException("DB connection lost"));

        SearchCategoriesQuery.Result result = searchCategoriesQuery.search(term);

        assertInstanceOf(SearchCategoriesQuery.Result.Error.class, result);

        SearchCategoriesQuery.Result.Error error = (SearchCategoriesQuery.Result.Error) result;
        assertEquals(ErrorCode.CATEGORIES_NOT_FOUND.getHttpStatus(), error.status()); // según tu ErrorCode.CATEGORIES_NOT_FOUND.getHttpStatus()
        assertTrue(error.message().contains("error searching categories"));
    }

}