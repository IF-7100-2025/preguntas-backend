package ucr.ac.cr.learningcommunity.questionservice.api.rests;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.ApiResponse;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.RandomCategoriesResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetRandomCategoriesQuery;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.SearchCategoriesQuery;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/private/questions/collaborator")
public class CollaboratorController {
    private final GetRandomCategoriesQuery getRandomCategoriesQuery;
    private final SearchCategoriesQuery searchCategoriesQuery;
    public  record  ListRandomCategoriesResponse (List<RandomCategoriesResponse> categories) {}

    public CollaboratorController(GetRandomCategoriesQuery getRandomCategoriesQuery, SearchCategoriesQuery searchCategoriesQuery) {
        this.searchCategoriesQuery = searchCategoriesQuery;
        this.getRandomCategoriesQuery = getRandomCategoriesQuery;
    }
    @GetMapping("/random-categories")
    public ResponseEntity<?> getRandomCategories() {
        var result = getRandomCategoriesQuery.query();

        return switch (result) {
            case GetRandomCategoriesQuery.Result.Success success ->
                    ResponseEntity.ok(new ListRandomCategoriesResponse(success.categories()));
            case GetRandomCategoriesQuery.Result.Error error ->
                    ResponseEntity.ok(new ApiResponse(error.status(), error.message()));
        };
    }

    @GetMapping("/search-categories")
    public ResponseEntity<?> searchCategories(
            @RequestParam(required = false) String term) {

        var result = searchCategoriesQuery.search(term);

        return switch (result) {
            case SearchCategoriesQuery.Result.Success success ->
                    ResponseEntity.ok(new ListRandomCategoriesResponse(success.categories()));
            case SearchCategoriesQuery.Result.Error error ->
                    ResponseEntity.status(error.status())
                            .body(new ApiResponse(error.status(),error.message()));
        };
    }
}