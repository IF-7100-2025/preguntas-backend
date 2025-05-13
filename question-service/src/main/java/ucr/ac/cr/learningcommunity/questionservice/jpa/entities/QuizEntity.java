package ucr.ac.cr.learningcommunity.questionservice.jpa.entities;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "quiz")

public class QuizEntity {
    @Id
    @Column(name = "id_quiz", nullable = false)
    private UUID id;

    @Column(name = "grade")
    private int grade;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private QuestionEntity question;

/*    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserEntity user;*/

    @Column(name = "completed")
    private LocalDateTime completed;

    @Column(name = "status", nullable = false)
    private String status;

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

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
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

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
