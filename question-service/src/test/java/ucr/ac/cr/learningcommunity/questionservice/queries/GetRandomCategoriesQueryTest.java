package ucr.ac.cr.learningcommunity.questionservice.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.RandomCategoriesResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetRandomCategoriesQuery;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl.GetRandomCategoriesQueryImpl;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.CategoryEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.CategoryRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetRandomCategoriesQueryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private GetRandomCategoriesQueryImpl getRandomCategoriesQuery;

    private List<CategoryEntity> testCategories;

    @BeforeEach
    void setUp() {
        CategoryEntity cat1 = new CategoryEntity();
        cat1.setId(1L);
        cat1.setName("Matemáticas");

        CategoryEntity cat2 = new CategoryEntity();
        cat2.setId(2L);
        cat2.setName("Ciencias");

        CategoryEntity cat3 = new CategoryEntity();
        cat3.setId(3L);
        cat3.setName("Historia");

        CategoryEntity cat4 = new CategoryEntity();
        cat4.setId(4L);
        cat4.setName("Geografía");

        CategoryEntity cat5 = new CategoryEntity();
        cat5.setId(5L);
        cat5.setName("Arte");

        CategoryEntity cat6 = new CategoryEntity();
        cat6.setId(6L);
        cat6.setName("Programación");

        testCategories = Arrays.asList(cat1, cat2, cat3, cat4, cat5, cat6);
    }

    @Test
    public void testRandomCategoriesSuccess() {
        when(categoryRepository.findAll()).thenReturn(testCategories);
        when(questionRepository.countByCategories_Id(anyLong())).thenReturn(10);

        GetRandomCategoriesQuery.Result result = getRandomCategoriesQuery.query();

        assertTrue(result instanceof GetRandomCategoriesQuery.Result.Success);
        List<RandomCategoriesResponse> responses = ((GetRandomCategoriesQuery.Result.Success) result).categories();
        assertNotNull(responses);
        assertEquals(5, responses.size());

        for (RandomCategoriesResponse res : responses) {
            assertNotNull(res.id());
            assertNotNull(res.name());
            assertEquals(10, res.questions());
        }
    }

    @Test
    void testDBExceptionRetrievingCategories() {

        when(categoryRepository.findAll()).thenThrow(new RuntimeException("DB error"));
        GetRandomCategoriesQuery.Result result = getRandomCategoriesQuery.query();
        assertTrue(result instanceof GetRandomCategoriesQuery.Result.Error);
        GetRandomCategoriesQuery.Result.Error error = (GetRandomCategoriesQuery.Result.Error) result;
        assertEquals(500, error.status());
        assertTrue(error.message().contains("Error retrieving random categories"));
    }
}