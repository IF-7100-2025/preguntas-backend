package ucr.ac.cr.learningcommunity.iaservice.api.rests;

import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.learningcommunity.iaservice.api.types.request.CategorizeSuggestionRequest;
import ucr.ac.cr.learningcommunity.iaservice.api.types.response.ApiResponse;
import ucr.ac.cr.learningcommunity.iaservice.handlers.commands.CategorizeQuestionHandler;
import ucr.ac.cr.learningcommunity.iaservice.models.BaseException;
import ucr.ac.cr.learningcommunity.iaservice.models.ErrorCode;

import org.springframework.http.ResponseEntity;
import ucr.ac.cr.learningcommunity.iaservice.service.CategoryEmbeddingService;

@RestController
@RequestMapping("/api/private/ia")
public class IAController {

    private final CategorizeQuestionHandler categorizeQuestionHandler;
    private final CategoryEmbeddingService categoryEmbeddingService;

    public IAController(CategorizeQuestionHandler categorizeQuestionHandler, CategoryEmbeddingService categoryEmbeddingService) {
        this.categorizeQuestionHandler = categorizeQuestionHandler;
        this.categoryEmbeddingService = categoryEmbeddingService;
    }

    @PostMapping("/categorize")
    public ResponseEntity<?> categorizeQuestion(@RequestBody CategorizeSuggestionRequest request) {
            var result = categorizeQuestionHandler.categorizeQuestion(new CategorizeQuestionHandler.Command(request.question()));
            return switch (result) {
                case CategorizeQuestionHandler.Result.Success success -> ResponseEntity.ok(success.categories());
                case CategorizeQuestionHandler.Result.InternalError internalError ->
                        throw BaseException.exceptionBuilder()
                                .code(ErrorCode.IA_SERVICE_ERROR)
                                .message("Error occurred while categorizing the question: " + internalError.msg())
                                .build();
                case CategorizeQuestionHandler.Result.Unauthorized unauthorized ->
                        throw BaseException.exceptionBuilder()
                                .code(ErrorCode.NOT_AUTHORIZED)
                                .message("Unauthorized access: " + unauthorized.msg())
                                .build();
                case CategorizeQuestionHandler.Result.ValidationError validationError ->
                        throw BaseException.exceptionBuilder()
                                .code(ErrorCode.VALIDATION_ERROR)
                                .message("Validation error: " + validationError.msg())
                                .build();
            };
        }

    @PostMapping("/updateCategoryEmbeddings")
    public ResponseEntity<?> updateCategoryEmbeddings() {
        try {
            String result = categoryEmbeddingService.updateCategoriesWithEmbeddings();
            if (result != null) {
                return ResponseEntity.ok(new ApiResponse(result, ErrorCode.OK.getHttpStatus()));
            } else {
                throw BaseException.exceptionBuilder()
                        .code(ErrorCode.IA_CATEGORY_SERVICE_ERROR)
                        .message("Failed to update category embeddings: null result")
                        .build();
            }
        } catch (Exception e) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.IA_CATEGORY_SERVICE_ERROR)
                    .message("Error updating category embeddings: " + e.getMessage())
                    .build();
        }
    }
}