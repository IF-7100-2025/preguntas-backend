package ucr.ac.cr.learningcommunity.questionservice.jpa.entities;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "quiz")

public class QuizEntity {
    @Id
    @Column(name = "id_quiz", nullable = false)
    private UUID id;

    @Column(name = "grade")
    private int grade;

    @ManyToMany
    @JoinTable(
            name = "quiz_question",
            joinColumns = @JoinColumn(name = "id_quiz"),
            inverseJoinColumns = @JoinColumn(name =  "question_id")
    )

    private Set<QuestionEntity> questions =  new HashSet<>();

    @Column(name = "completed")
    private LocalDateTime completed;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;


    @Column(name = "end_time")
    private LocalDateTime endTime;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Set<QuestionEntity> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<QuestionEntity> questions) {
        this.questions = questions;
    }

    public LocalDateTime getCompleted() {
        return completed;
    }

    public void setCompleted(LocalDateTime completed) {
        this.completed = completed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
