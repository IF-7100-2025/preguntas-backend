package ucr.ac.cr.learningcommunity.questionservice.api.rests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.CategoryResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.CreateCategoryHandler;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.CategoryRequest;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetCategoriesQuery;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CreateCategoryHandler createCategoryHandler;
    private final GetCategoriesQuery getCategoriesQuery;

    @Autowired
    public CategoryController(CreateCategoryHandler createCategoryHandler,
                              GetCategoriesQuery getCategoriesQuery) {
        this.createCategoryHandler = createCategoryHandler;
        this.getCategoriesQuery = getCategoriesQuery;
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request) {
        var result = createCategoryHandler.createCategory(new CreateCategoryHandler.Command(request.name()));

        return switch (result) {
            case CreateCategoryHandler.Result.Success success ->
                    ResponseEntity.status(201).body(success);
            case CreateCategoryHandler.Result.InternalError internalError ->
                    ResponseEntity.status(internalError.status()).body(internalError);
        };
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        var result = getCategoriesQuery.query();

        if (result instanceof GetCategoriesQuery.Result.NoCategoriesFound) {
            return ResponseEntity.status(200).body(Collections.emptyList());
        }
        GetCategoriesQuery.Result.Success successResult = (GetCategoriesQuery.Result.Success) result;
        List<CategoryResponse> categoryResponses = successResult.categories().stream()
                .map(category -> new CategoryResponse(category.getId(), category.getName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(categoryResponses);
    }
}