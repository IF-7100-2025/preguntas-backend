package ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl;

import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetCategoriesQuery;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.CategoryEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.CategoryRepository;

import java.util.List;

@Service
public class GetCategoriesQueryImpl implements GetCategoriesQuery {

    private final CategoryRepository categoryRepository;

    public GetCategoriesQueryImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Result query() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {return new Result.NoCategoriesFound();}
        return new Result.Success(categories);
    }
}
