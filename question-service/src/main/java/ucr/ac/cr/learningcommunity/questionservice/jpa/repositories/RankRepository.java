package ucr.ac.cr.learningcommunity.questionservice.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.RankEntity;

import java.util.List;
import java.util.Optional;

public interface RankRepository extends JpaRepository<RankEntity, String> {

    @Query(value = "SELECT * FROM ranks WHERE :xpAmount >= min_xp AND (:xpAmount <= max_xp OR max_xp IS NULL) ORDER BY min_xp DESC LIMIT 1",
            nativeQuery = true)
    Optional<RankEntity> findRankByXp(@Param("xpAmount") int xpAmount);

    @Query("SELECT r FROM RankEntity r WHERE :xpAmount >= r.minXP AND (:xpAmount <= r.maxXP OR r.maxXP IS NULL) ORDER BY r.minXP DESC")
    List<RankEntity> findRanksByXp(@Param("xpAmount") int xpAmount);

    @Query(value = "SELECT * FROM ranks WHERE min_xp > ? ORDER BY min_xp ASC LIMIT 1",
            nativeQuery = true)
    Optional<RankEntity> findNextRankByCurrentXP(int currentXP);

    @Query("SELECT r FROM RankEntity r ORDER BY r.minXP ASC")
    List<RankEntity> findAllRanksOrdered();
}