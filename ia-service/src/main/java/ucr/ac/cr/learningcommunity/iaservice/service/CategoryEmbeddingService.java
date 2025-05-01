package ucr.ac.cr.learningcommunity.iaservice.service;

import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.learningcommunity.iaservice.jpa.entities.CategoryEmbedding;
import ucr.ac.cr.learningcommunity.iaservice.jpa.entities.CategoryEntity;
import ucr.ac.cr.learningcommunity.iaservice.jpa.repositories.CategoryEmbeddingRepository;
import ucr.ac.cr.learningcommunity.iaservice.jpa.repositories.CategoryRepository;
import ucr.ac.cr.learningcommunity.iaservice.models.BaseException;
import ucr.ac.cr.learningcommunity.iaservice.models.ErrorCode;

import java.util.List;

@Service
public class CategoryEmbeddingService {

    @Autowired
    private final OpenAiEmbeddingClient embeddingClient;
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final CategoryEmbeddingRepository categoryEmbeddingRepository;

    @Autowired
    public CategoryEmbeddingService(OpenAiEmbeddingClient embeddingClient,
                                    CategoryRepository categoryRepository,
                                    CategoryEmbeddingRepository categoryEmbeddingRepository) {
        this.embeddingClient = embeddingClient;
        this.categoryRepository = categoryRepository;
        this.categoryEmbeddingRepository = categoryEmbeddingRepository;
    }

    @Transactional  // dura aproximadamente 4 minutos en ponerle embeddeing a 183 categorias
    public String updateCategoriesWithEmbeddings() {
        try {
        List<CategoryEntity> categories = categoryRepository.findAll();
        for (CategoryEntity category : categories) {
            try {
                List<Double> embedding = embeddingClient.embed(category.getName());
                CategoryEmbedding existingEmbedding = categoryEmbeddingRepository.findByCategoryId(category.getId());
                if (existingEmbedding != null) {
                    existingEmbedding.setEmbedding(embedding);
                    categoryEmbeddingRepository.save(existingEmbedding);
                } else {
                    CategoryEmbedding newEmbedding = new CategoryEmbedding(category, embedding);
                    categoryEmbeddingRepository.save(newEmbedding);
                }
            } catch (Exception e) {
                throw BaseException.exceptionBuilder()
                        .code(ErrorCode.IA_CATEGORY_SERVICE_ERROR)
                        .message(ErrorCode.IA_CATEGORY_SERVICE_ERROR.getDefaultMessage() + " Error embedding category " + category.getName())
                        .build();
            }
        }
        return "Embeddings updated successfully.";
    } catch (Exception e) {
        throw BaseException.exceptionBuilder()
                .code(ErrorCode.IA_CATEGORY_SERVICE_ERROR)
                .message(ErrorCode.IA_CATEGORY_SERVICE_ERROR.getDefaultMessage() + " Error updating embeddings"+e.getMessage())
                .build();
    }
    }
}

