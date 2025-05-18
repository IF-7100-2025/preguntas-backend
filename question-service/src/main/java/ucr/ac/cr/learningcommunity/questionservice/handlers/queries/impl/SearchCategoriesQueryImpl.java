package ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl;

import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.RandomCategoriesResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.SearchCategoriesQuery;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.CategoryEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.CategoryRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.util.Collections;
import java.util.List;

@Service
public class SearchCategoriesQueryImpl implements SearchCategoriesQuery {
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    public SearchCategoriesQueryImpl(CategoryRepository categoryRepository, QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
    }
    @Override
    public Result search(String term) {
        try {
            if (term == null || term.isBlank()) {
                return new Result.Success(Collections.emptyList());
            }
            List<CategoryEntity> categories = categoryRepository
                    .findByNameContainingIgnoreCase(term);

            List<RandomCategoriesResponse> response = categories.stream()
                    .map(c -> new RandomCategoriesResponse(
                            c.getId().toString(),
                            c.getName(),
                            questionRepository.countByCategories_Id(c.getId())
                    ))
                    .toList();

            return new Result.Success(response);
        } catch (Exception e) {
            return new Result.Error(ErrorCode.CATEGORIES_NOT_FOUND.getHttpStatus(), ErrorCode.CATEGORIES_NOT_FOUND.getDefaultMessage()+" ,error searching categories: " + e.getMessage());
        }
    }
}
