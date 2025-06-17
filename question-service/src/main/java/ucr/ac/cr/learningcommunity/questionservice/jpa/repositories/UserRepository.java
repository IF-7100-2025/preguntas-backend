package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    @Query(value = """
        SELECT 
            CASE
                WHEN CURRENT_DATE - DATE(last_activity) = 1 THEN daily_streak + 1
                WHEN CURRENT_DATE - DATE(last_activity) = 0 THEN daily_streak
                ELSE 1
            END
        FROM users
        WHERE id_user = :userId
    """, nativeQuery = true)
    int calculateNewStreak(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE users
        SET 
            xp_amount = xp_amount + 10,
            daily_streak = :newStreak,
            last_activity = CURRENT_DATE
        WHERE id_user = :userId
    """, nativeQuery = true)
    void updateProgress(@Param("userId") String userId, @Param("newStreak") int newStreak);

    // Ya existentes
    Optional<UserEntity> findById(String id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
}
