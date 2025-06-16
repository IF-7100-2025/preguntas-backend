package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    @Modifying
    @Query("""
    UPDATE UserEntity u
    SET 
        u.xpAmount = u.xpAmount + 10,
        u.lastActivity = CURRENT_TIMESTAMP,
        u.dailyStreak = CASE
            WHEN CURRENT_DATE - CAST(u.lastActivity AS date) = 1 THEN u.dailyStreak + 1
            WHEN CURRENT_DATE - CAST(u.lastActivity AS date) = 0 THEN u.dailyStreak
            ELSE 1
        END
    WHERE u.id = :userId
""")
    void updateProgressOnQuestionCreation(@Param("userId") String userId);

    Optional<UserEntity> findById(String id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
}
