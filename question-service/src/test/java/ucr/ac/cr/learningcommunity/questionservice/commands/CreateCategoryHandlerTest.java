package ucr.ac.cr.learningcommunity.questionservice.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateCategoryHandler.Command;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateCategoryHandler.Result;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl.CreateCategoryHandlerImpl;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.CategoryRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateCategoryHandlerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CreateCategoryHandlerImpl createCategoryHandler;

    private Command validCommand;

    @BeforeEach
    void setUp() {
        validCommand = new Command("Ciencias");
    }

    @Test
    void testCreateCategorySuccess() {
        when(categoryRepository.existsByName("Ciencias")).thenReturn(false);

        Result result = createCategoryHandler.createCategory(validCommand);

        assertInstanceOf(Result.Success.class, result);
        Result.Success success = (Result.Success) result;
        assertEquals("Category created successfully", success.msg());
        verify(categoryRepository).save(any());
    }

    @Test
    void testCreateCategoryIsNull() {
        Command invalidCommand = new Command("  ");
        BaseException ex = assertThrows(BaseException.class, () ->
                createCategoryHandler.createCategory(invalidCommand));
        assertTrue(ex.getMessage().contains("Category name is required"));
    }

    @Test
    void testCreateCategoryTAlreadyExists() {
        when(categoryRepository.existsByName("Ciencias")).thenReturn(true);

        BaseException ex = assertThrows(BaseException.class, () ->
                createCategoryHandler.createCategory(validCommand));
        assertEquals("Category already exists", ex.getMessage());
    }
}
