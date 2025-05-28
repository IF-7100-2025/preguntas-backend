package ucr.ac.cr.learningcommunity.iaservice.jpa.entities;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "category_embedding")
public class CategoryEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "SERIAL")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, referencedColumnName = "id")
    private CategoryEntity category;

    @Column(name = "embedding", nullable = false, columnDefinition = "FLOAT8[]")
    private List<Double> embedding;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public List<Double> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Double> embedding) {
        this.embedding = embedding;
    }
    public CategoryEmbedding(CategoryEntity category, List<Double> embedding) {
        this.category = category;
        this.embedding = embedding;
    }
    public CategoryEmbedding() {}
}