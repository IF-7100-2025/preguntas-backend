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
import java.util.Map;
import java.util.stream.Collectors;

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

      // dura aproximadamente 1.7 segs en ponerle embeddeing a 438 categorias
      public String updateCategoriesWithEmbeddings() {
          List<CategoryEntity> categories = categoryRepository.findAll();
          Map<Long, CategoryEmbedding> existingEmbeddings = categoryEmbeddingRepository.findAll().stream()
                  .collect(Collectors.toMap(e -> e.getCategory().getId(), e -> e));
          categories.parallelStream().forEach(category -> {
              try {
                  CategoryEmbedding existing = existingEmbeddings.get(category.getId());
                  if (existing != null && existing.getEmbedding() != null && !existing.getEmbedding().isEmpty()) {
                      return;
                  }
                  List<Double> embedding = embeddingClient.embed(category.getName());
                  CategoryEmbedding embeddingEntity = existingEmbeddings.get(category.getId());
                  if (embeddingEntity != null) {
                      embeddingEntity.setEmbedding(embedding);
                  } else {
                      embeddingEntity = new CategoryEmbedding(category, embedding);
                  }
                  categoryEmbeddingRepository.save(embeddingEntity);
              } catch (Exception e) {
                  System.err.println("Error embedding category: " + category.getName() + " -> " + e.getMessage());
              }
          });
          return "Embeddings updated successfully.";
      }

}

