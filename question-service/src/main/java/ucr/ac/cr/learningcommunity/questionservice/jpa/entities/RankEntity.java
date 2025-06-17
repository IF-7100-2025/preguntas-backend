package ucr.ac.cr.learningcommunity.questionservice.jpa.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "ranks") // Cambiar nombre de tabla por consistencia
public class RankEntity {

    @Id
    @Column(nullable = false, length = 20)
    private String rank;

    @Column(name = "min_xp", nullable = false)
    private int minXP;

    @Column(name = "max_xp", nullable = true) // Permitir NULL para el rango más alto
    private Integer maxXP; // Cambiar a Integer para permitir null

    public RankEntity() {
    }

    public RankEntity(String rank, int minXP, Integer maxXP) {
        this.rank = rank;
        this.minXP = minXP;
        this.maxXP = maxXP;
    }

    // Getters y setters
    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getMinXP() {
        return minXP;
    }

    public void setMinXP(int minXP) {
        this.minXP = minXP;
    }

    public Integer getMaxXP() {
        return maxXP;
    }

    public void setMaxXP(Integer maxXP) {
        this.maxXP = maxXP;
    }

    @Override
    public String toString() {
        return "RankEntity{" +
                "rank='" + rank + '\'' +
                ", minXP=" + minXP +
                ", maxXP=" + maxXP +
                '}';
    }
}