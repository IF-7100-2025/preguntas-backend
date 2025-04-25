package ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateQuestionHandler;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.CategoryEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.CategoryRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.util.Base64;
import java.util.List;
@Service
public class CreateQuestionHandlerImpl implements CreateQuestionHandler {
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;

    public CreateQuestionHandlerImpl(QuestionRepository questionRepository,
                                     CategoryRepository categoryRepository) {
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @Override
    public Result createQuestion(Command command) {
        if (command.question() == null || command.question().isBlank()) {
            throw validationError("Question is required");
        }

        if (command.categories() == null || command.categories().isEmpty()) {
            throw validationError("At least one category is required");
        }

        if (command.categories().size() > 3) {
            throw validationError("Maximum 3 categories allowed");
        }

        List<CategoryEntity> categoryEntities = categoryRepository.findAllByNameIn(command.categories());

        if (categoryEntities.size() != command.categories().size()) {
            List<String> foundCategories = categoryEntities.stream()
                    .map(CategoryEntity::getName)
                    .toList();

            List<String> missingCategories = command.categories().stream()
                    .filter(c -> !foundCategories.contains(c))
                    .toList();

            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.CATEGORIES_NOT_FOUND)
                    .message("The following categories don't exist: " + missingCategories)
                    .build();
        }
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setText(command.question());
        if (command.imageBase64() != null) {
            String cleanBase64 = cleanBase64(command.imageBase64());
            if (!isValidBase64(cleanBase64)) {
                throw validationError("Invalid image format");
            }
            questionEntity.setImage(cleanBase64.getBytes());
        }        questionEntity.getCategories().addAll(categoryEntities);
        questionRepository.save(questionEntity);
        return new Result.Success(200,"Question created successfully");
    }

    private boolean isValidBase64(String base64) {
        if (base64 == null || base64.isBlank()) return false;
        String cleanBase64 = base64
                .replaceFirst("^data:[^;]+;base64,", "")
                .replaceAll("\\s", "");

        try {
            Base64.getDecoder().decode(cleanBase64);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    private String cleanBase64(String base64) {
        return base64.replaceFirst("^data:[^;]+;base64,", "").replaceAll("\\s", "");
    }
    private BaseException validationError(String message) {
        return BaseException.exceptionBuilder()
                .code(ErrorCode.VALIDATION_ERROR)
                .message(message)
                .build();
    }
}