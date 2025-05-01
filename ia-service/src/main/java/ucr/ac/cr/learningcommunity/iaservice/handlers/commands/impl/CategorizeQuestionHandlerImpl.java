package ucr.ac.cr.learningcommunity.iaservice.handlers.commands.impl;

import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.iaservice.api.types.response.CategoryResponse;
import ucr.ac.cr.learningcommunity.iaservice.handlers.commands.CategorizeQuestionHandler;
import ucr.ac.cr.learningcommunity.iaservice.jpa.entities.CategoryEmbedding;
import ucr.ac.cr.learningcommunity.iaservice.jpa.repositories.CategoryEmbeddingRepository;
import ucr.ac.cr.learningcommunity.iaservice.models.ErrorCode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategorizeQuestionHandlerImpl implements CategorizeQuestionHandler {

    private final OpenAiEmbeddingClient embeddingClient;
    private final CategoryEmbeddingRepository categoryEmbeddingRepository;

    @Autowired
    public CategorizeQuestionHandlerImpl(OpenAiEmbeddingClient embeddingClient, CategoryEmbeddingRepository categoryEmbeddingRepository) {
        this.embeddingClient = embeddingClient;
        this.categoryEmbeddingRepository = categoryEmbeddingRepository;
    }

    @Override
    public Result categorizeQuestion(Command command) {
        if (command.questionText() == null || command.questionText().isBlank()) {
            return new Result.ValidationError(ErrorCode.VALIDATION_ERROR.getHttpStatus(), "The question text cannot be empty.");
        }
        if (command.questionText().length() > 200 || command.questionText().length()<20 ) {
            return new Result.ValidationError(ErrorCode.VALIDATION_ERROR.getHttpStatus(), "The question text not longer valid.");
        }
        try {
            List<Double> questionEmbedding = embeddingClient.embed(command.questionText());
            List<CategoryEmbedding> categoryEmbeddings = categoryEmbeddingRepository.findAll();
            List<CategoryResponse> bestCategories = getTopCategories(questionEmbedding, categoryEmbeddings, 3);
            return new Result.Success(bestCategories);
        } catch (NonTransientAiException e) {
            return new Result.InternalError(ErrorCode.NOT_AUTHORIZED.getHttpStatus(), ErrorCode.NOT_AUTHORIZED.getDefaultMessage()+"\n"+e.getMessage());
        } catch (Exception e) {
            return new Result.InternalError(ErrorCode.IA_SERVICE_ERROR.getHttpStatus(), ErrorCode.IA_SERVICE_ERROR.getDefaultMessage()+"\n"+e.getMessage());
        }
    }

    private List<CategoryResponse> getTopCategories(List<Double> questionEmbedding,
                                                    List<CategoryEmbedding> categoryEmbeddings,
                                                    int topN) {
        return categoryEmbeddings.stream()
                .map(categoryEmbedding -> {
                    List<Double> categoryVec = categoryEmbedding.getEmbedding();
                    double similarity = calculateCosineSimilarity(questionEmbedding, categoryVec);
                    return new CategorySimilarity(
                            categoryEmbedding.getCategory().getId(),
                            categoryEmbedding.getCategory().getName(),
                            similarity);
                })
                .sorted((a, b) -> Double.compare(b.similarity(), a.similarity()))
                .limit(topN)
                .map(categorySimilarity -> new CategoryResponse(
                        categorySimilarity.categoryId(),
                        categorySimilarity.categoryName()))
                .collect(Collectors.toList());
    }

    private double calculateCosineSimilarity(List<Double> vecA, List<Double> vecB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vecA.size(); i++) {
            dotProduct += vecA.get(i) * vecB.get(i);
            normA += Math.pow(vecA.get(i), 2);
            normB += Math.pow(vecB.get(i), 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));// similitud de coseno
    }

    private record CategorySimilarity(Long categoryId, String categoryName, double similarity) {}
}
