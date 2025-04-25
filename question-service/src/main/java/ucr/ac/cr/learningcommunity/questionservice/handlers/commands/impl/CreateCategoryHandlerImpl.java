package ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateCategoryHandler;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.CategoryEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.CategoryRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

@Service
public class CreateCategoryHandlerImpl implements CreateCategoryHandler {

    private final CategoryRepository categoryRepository;

    public CreateCategoryHandlerImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @Override
    public Result createCategory(Command command) {
        if (command.name() == null || command.name().isBlank()) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.VALIDATION_ERROR)
                    .message(ErrorCode.VALIDATION_ERROR.getDefaultMessage()+ ", Category name is required")
                    .build();
        }
        if (categoryRepository.existsByName(command.name())) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.CATEGORY_ALREADY_EXISTS)
                    .message("Category already exists")
                    .build();
        }
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(command.name());
        categoryRepository.save(categoryEntity);
        return new Result.Success(201, "Category created successfully");
    }
}
