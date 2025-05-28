package ucr.ac.cr.learningcommunity.questionservice.api.rests;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.ChangePasswordRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.UpdateProfileRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.ApiResponse;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.RandomCategoriesResponse;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.UserProfileResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.ChangePasswordHandler;
import ucr.ac.cr.learningcommunity.questionservice.handlers.commands.UpdateProfileHandler;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetRandomCategoriesQuery;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetUserInformationProfileQuery;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.SearchCategoriesQuery;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/private/questions/collaborator")
public class CollaboratorController {
    private final GetRandomCategoriesQuery getRandomCategoriesQuery;
    private final SearchCategoriesQuery searchCategoriesQuery;
    private final GetUserInformationProfileQuery getUserProfileQuery;

    private final UpdateProfileHandler updateProfileHandler;
    private final ChangePasswordHandler changePasswordHandler;
    public  record  ListRandomCategoriesResponse (List<RandomCategoriesResponse> categories) {}

    public CollaboratorController(GetRandomCategoriesQuery getRandomCategoriesQuery,
                                  SearchCategoriesQuery searchCategoriesQuery,
                                  GetUserInformationProfileQuery getUserProfileQuery,
                                  UpdateProfileHandler updateProfileHandler,
                                  ChangePasswordHandler changePasswordHandler) {
        this.searchCategoriesQuery = searchCategoriesQuery;
        this.getRandomCategoriesQuery = getRandomCategoriesQuery;
        this.getUserProfileQuery = getUserProfileQuery;
        this.updateProfileHandler = updateProfileHandler;
        this.changePasswordHandler = changePasswordHandler;
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
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(
            @RequestHeader("id") String userId) {
        var result = getUserProfileQuery.getUserInformationProfile(userId);
        return switch (result) {
            case GetUserInformationProfileQuery.Result.Success success ->
                    ResponseEntity.ok(new UserProfileResponse(
                            success.userInformationProfile().username(),
                            success.userInformationProfile().email(),
                            success.userInformationProfile().profileImage()
                    ));
            case GetUserInformationProfileQuery.Result.Error error ->
                    ResponseEntity.status(error.status())
                            .body(new ApiResponse(error.status(),error.message()));
        };
    }
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("id") String userId,
            @RequestBody UpdateProfileRequest request) {

        var result = updateProfileHandler.updateProfile(userId, request);

        return switch (result) {
            case UpdateProfileHandler.Result.Success success ->
                    ResponseEntity.ok(new ApiResponse(200, success.message()));
            case UpdateProfileHandler.Result.Error error ->
                    ResponseEntity.status(error.status())
                            .body(new ApiResponse(error.status(), error.message()));
        };
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("id") String userId,
            @RequestBody ChangePasswordRequest request) {
        var result = changePasswordHandler.changePassword(userId, request);

        return switch (result) {
            case ChangePasswordHandler.Result.Success success ->
                    ResponseEntity.ok(new ApiResponse(200, success.message()));
            case ChangePasswordHandler.Result.Error error ->
                    ResponseEntity.status(error.status())
                            .body(new ApiResponse(error.status(), error.message()));
        };
    }
}