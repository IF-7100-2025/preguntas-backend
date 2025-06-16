package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findById(String id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
}
