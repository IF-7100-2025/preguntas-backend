package ucr.ac.cr.learningcommunity.questionservice.jpa.entities;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "question_report")
public class QuestionReportEntity {

    @Id
    @Column(name = "id_report", nullable = false)
    private UUID idReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_question", nullable = false)
    private QuestionEntity question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity user;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "reported_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date reportedAt = new Date();

    @Column(name = "status", length = 50)
    private String status;

    public QuestionReportEntity() {
        // JPA necesita constructor vacío
    }

    public QuestionReportEntity(UUID idReport,
                                QuestionEntity question,
                                UserEntity user,
                                String reason,
                                String description,
                                Date reportedAt,
                                String status) {
        this.idReport = idReport;
        this.question = question;
        this.user = user;
        this.reason = reason;
        this.description = description;
        this.reportedAt = (reportedAt != null ? reportedAt : new Date());
        this.status = status;
    }

    // Si prefieres generar el UUID en la aplicación antes de persistir:
    // al crear la instancia: new QuestionReportEntity(UUID.randomUUID(), question, user, ..., new Date(), "PENDING")

    public UUID getIdReport() {
        return idReport;
    }

    public void setIdReport(UUID idReport) {
        this.idReport = idReport;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getReportedAt() {
        return reportedAt;
    }

    // No exponer setter de reportedAt si quieres inmutable; si sí, descomenta:
    // public void setReportedAt(Date reportedAt) { this.reportedAt = reportedAt; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionReportEntity)) return false;
        QuestionReportEntity that = (QuestionReportEntity) o;
        return idReport != null && idReport.equals(that.idReport);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idReport);
    }

    @Override
    public String toString() {
        return "QuestionReportEntity{" +
                "idReport=" + idReport +
                ", questionId=" + (question != null ? question.getId() : null) +
                ", userId=" + (user != null ? user.getId() : null) +
                ", reason='" + reason + '\'' +
                ", reportedAt=" + reportedAt +
                ", status='" + status + '\'' +
                '}';
    }
}
