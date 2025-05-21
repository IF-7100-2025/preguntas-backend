package ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl;

import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.RandomCategoriesResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetRandomCategoriesQuery;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.CategoryEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.CategoryRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;

import java.util.Collections;
import java.util.List;
@Service
public class GetRandomCategoriesQueryImpl implements GetRandomCategoriesQuery {
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;

    public GetRandomCategoriesQueryImpl(CategoryRepository categoryRepository , QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
    }
    @Override
    public Result query() {
        try {
            List<CategoryEntity> categories = categoryRepository.findAll();
            Collections.shuffle(categories);

            List<RandomCategoriesResponse> randomCategories = categories.stream()
                    .limit(5)
                    .map(c -> {
                        int questionCount = questionRepository.countByCategories_Id(c.getId());
                        return new RandomCategoriesResponse(
                                c.getId().toString(),
                                c.getName(),
                                questionCount
                        );
                    })
                    .toList();

            return new Result.Success(randomCategories);
        } catch (Exception e) {
            return new Result.Error(500, "Error retrieving random categories: " + e.getMessage());
        }
    }
}
