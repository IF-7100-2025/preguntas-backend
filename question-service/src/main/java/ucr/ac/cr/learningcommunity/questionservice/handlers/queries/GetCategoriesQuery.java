package ucr.ac.cr.learningcommunity.questionservice.handlers.queries;

import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.CategoryEntity;

import java.util.List;

public interface GetCategoriesQuery {
    Result query();
    sealed interface Result {
        record Success(List<CategoryEntity> categories) implements Result {}
        record NoCategoriesFound() implements Result {}
    }
}
