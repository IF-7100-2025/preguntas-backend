package ucr.ac.cr.learningcommunity.questionservice.jpa.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class CategoryEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Usamos SERIAL en PostgreSQL
    private Long id;

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
