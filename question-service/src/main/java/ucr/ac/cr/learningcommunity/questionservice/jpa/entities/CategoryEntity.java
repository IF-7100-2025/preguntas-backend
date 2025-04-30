package ucr.ac.cr.learningcommunity.questionservice.jpa.entities;

import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name = "category")
public class CategoryEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Usamos SERIAL en PostgreSQL
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
