package ucr.ac.cr.learningcommunity.questionservice.handlers.commands.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.QuestionRequest;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateQuestionHandler;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.AnswerOptionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.CategoryEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.AnswerOptionRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.CategoryRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.UserRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class CreateQuestionHandlerImpl implements CreateQuestionHandler {
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final UserRepository userRepository;
    private static final int MAX_IMAGE_SIZE = 2_097_152; // Límite de 2MB

    public CreateQuestionHandlerImpl(QuestionRepository questionRepository,
                                     CategoryRepository categoryRepository,
                                     AnswerOptionRepository answerOptionRepository,
                                     UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Result createQuestion(QuestionRequest request, String userId) {
        validateRequest(request, userId);
        List<CategoryEntity> categories = processCategories(request.categories());
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> validationError("User not found"));
        QuestionEntity question = createQuestionEntity(request, categories, user);
        processAnswerOptions(request.answerOptions(), question);
        return new Result.Success(201, "Question created successfully");
    }

    private void validateRequest(QuestionRequest request, String id) {
        validateUser(id);
        validateQuestionText(request.text());
        validateAnswerOptions(request.answerOptions());
        validateCategories(request.categories());
        validateExplanation(request.explanation());

    }
    private void validateUser(String id) {
        if (id == null || id.isBlank()) {
            throw validationError("User id is required");
        }
    }
    private void validateQuestionText(String text) {
        if (text == null || text.isBlank()) {
            throw validationError("Question text is required");
        }

        if (text.length() > 210 || text.length() < 10) {
            throw validationError("Question text must be between 10 and 210 characters");
        }
    }
    private void validateExplanation(String explanation) {
        if (explanation == null || explanation.isBlank()) {
            throw validationError("Explanation is required");
        }

        if (explanation.length() < 10 || explanation.length() > 999) {
            throw validationError("Explanation is no longer valid");
        }
    }
    private void validateCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            throw validationError("At least one category is required");
        }

        if (categories.size() > 3) {
            throw validationError("Maximum 3 categories allowed");
        }
        if (categories.size() != categories.stream().distinct().count()) {
            throw validationError("Duplicate categories are not allowed");
        }
    }

    private void validateAnswerOptions(List<QuestionRequest.AnswerOptionRequest> answerOptions) {
        if (answerOptions == null || answerOptions.isEmpty()) {
            throw validationError("At least two answer options are required");
        }
        answerOptions.forEach(option -> {
            if (option.text() == null || option.text().isBlank() || option.text().length() > 150) {
                throw validationError("Answer option text is not valid text or is too long");
            }
        });
        long correctCount = answerOptions.stream()
                .filter(QuestionRequest.AnswerOptionRequest::isCorrect)
                .count();
        if (correctCount == answerOptions.size()) {
            throw validationError("All answers cannot be correct");
        }
        if (correctCount < 1) {
            throw validationError("At least one correct answer is required");
        }
    }

    private List<CategoryEntity> processCategories(List<String> categoryNames) {
        if (categoryNames.size() != categoryNames.stream().distinct().count()) {
            throw validationError("Duplicate categories are not allowed");
        }
        List<CategoryEntity> existingCategories = categoryRepository.findAllByNameIn(categoryNames);
        List<String> missingCategories = categoryNames.stream()
                .filter(name -> existingCategories.stream().noneMatch(c -> c.getName().equals(name)))
                .toList();
        if (!missingCategories.isEmpty()) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.CATEGORIES_NOT_FOUND)
                    .message("The following categories don't exist: " + missingCategories)
                    .build();
        }

        return existingCategories;
    }

    private QuestionEntity createQuestionEntity(QuestionRequest request, List<CategoryEntity> categories, UserEntity user) {
        QuestionEntity question = new QuestionEntity();
        question.setText(request.text());
        question.setId(UUID.randomUUID());
        question.setCreatedBy(user);
        question.setExplanation(request.explanation());
        question.setLikes(0);
        question.setDislikes(0);
        if (request.imageBase64() != null && !request.imageBase64().isBlank()) {
            processQuestionImage(request.imageBase64(), question);
        }
        question.getCategories().addAll(categories);
        return questionRepository.save(question);
    }

    private void processAnswerOptions(List<QuestionRequest.AnswerOptionRequest> optionRequests, QuestionEntity question) {
        for (QuestionRequest.AnswerOptionRequest optionRequest : optionRequests) {
            AnswerOptionEntity option = new AnswerOptionEntity();
            option.setQuestion(question);
            option.setText(optionRequest.text());
            option.setCorrect(optionRequest.isCorrect());
            answerOptionRepository.save(option);
        }
    }
    private void processQuestionImage(String base64, QuestionEntity question) {
        String cleanBase64 = cleanBase64(base64);

        if (!isValidBase64(cleanBase64)) {
            throw validationError("Invalid image format");
        }

        byte[] imageBytes = Base64.getDecoder().decode(cleanBase64);

        if (imageBytes.length > MAX_IMAGE_SIZE) {
            throw validationError("Image size exceeds maximum allowed (2MB)");
        }

        question.setImage(imageBytes);
    }

    private boolean isValidBase64(String base64) {
        try {
            Base64.getDecoder().decode(base64);
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