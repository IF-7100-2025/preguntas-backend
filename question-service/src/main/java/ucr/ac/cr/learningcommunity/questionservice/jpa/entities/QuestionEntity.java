package ucr.ac.cr.learningcommunity.questionservice.jpa.entities;

import jakarta.persistence.*;

import java.util.*;


@Entity
@Table(name = "question")
public class QuestionEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String text;

    @Column(name = "image", columnDefinition = "BYTEA")
    private byte[] image;

    @ManyToMany(mappedBy = "questions")
    private List<QuizEntity> quizzes;

    @ManyToMany
    @JoinTable(
            name = "question_category",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<CategoryEntity> categories = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdAt = new Date();

    @Column(length = 1000)
    private String explanation;

    @Column(name = "likes_count")
    private Integer likes = 0;

    @Column(name = "dislikes_count")
    private Integer dislikes = 0;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AnswerOptionEntity> answerOptions = new HashSet<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Set<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryEntity> categories) {
        this.categories = categories;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<QuizEntity> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<QuizEntity> quizzes) {
        this.quizzes = quizzes;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    public Set<AnswerOptionEntity> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(Set<AnswerOptionEntity> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
