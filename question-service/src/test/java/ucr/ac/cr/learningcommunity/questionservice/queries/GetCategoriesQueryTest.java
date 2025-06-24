package ucr.ac.cr.learningcommunity.questionservice.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetCategoriesQuery;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl.GetCategoriesQueryImpl;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.CategoryEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.CategoryRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetCategoriesQueryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private GetCategoriesQueryImpl getCategoriesQuery;

    private CategoryEntity category1;
    private CategoryEntity category2;

    @BeforeEach
    void setUp() {
        category1 = new CategoryEntity();
        category1.setId(1L);
        category1.setName("Finanzas");

        category2 = new CategoryEntity();
        category2.setId(2L);
        category2.setName("Desarrollo Web");
    }

    @Test
    void testReturnsSuccessWithCategories() {
        List<CategoryEntity> mockCategories = Arrays.asList(category1, category2);
        when(categoryRepository.findAll()).thenReturn(mockCategories);

        GetCategoriesQuery.Result result = getCategoriesQuery.query();

        assertTrue(result instanceof GetCategoriesQuery.Result.Success);
        List<CategoryEntity> categories = ((GetCategoriesQuery.Result.Success) result).categories();
        assertEquals(2, categories.size());
        assertEquals("Finanzas", categories.get(0).getName());
        assertEquals("Desarrollo Web", categories.get(1).getName());
    }

    @Test
    void testThrowsExceptionWhenRepositoryFails() {
        when(categoryRepository.findAll()).thenThrow(new RuntimeException("Database failure"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            getCategoriesQuery.query();
        });

        assertEquals("Database failure", exception.getMessage());
    }

}