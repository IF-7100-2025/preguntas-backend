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

    public CreateQuestionHandlerImpl(QuestionRepository questionRepository, CategoryRepository categoryRepository) {
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
    }
    @Transactional
    @Override
    public Result  createQuestion(Command command) {
        // get the categories from the database based on the names provided in the request
        List<String> categories = command.categories();
        List<CategoryEntity> categoryEntities = categoryRepository.findAllByNameIn(categories); // get the categories from the database based on the names provided in the request

        // if no categories were found
        if (categoryEntities.isEmpty()) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.CATEGORIES_NOT_FOUND)
                    .message("Categories not found in the database")
                    .build();
        }
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setText(command.question());
        if (command.imageBase64() != null) {// if is optional image do the verification
            byte[] decodedImage = decodeBase64Image(command.imageBase64());
            questionEntity.setImage(decodedImage);
        }
        QuestionEntity savedQuestion = questionRepository.save(questionEntity);
        // associate the categories with the question
        categoryEntities.forEach(categoryEntity -> {
            savedQuestion.getCategories().add(categoryEntity);
        });
        questionRepository.save(savedQuestion);
        return new Result.Success(200, "Question created successfully");
    }
    private byte[] decodeBase64Image(String base64Image) {
        return Base64.getDecoder().decode(base64Image);
    }
}