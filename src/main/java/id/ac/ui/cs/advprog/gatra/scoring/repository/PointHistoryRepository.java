package id.ac.ui.cs.advprog.gatra.scoring.repository;

import id.ac.ui.cs.advprog.gatra.scoring.model.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, UUID> {

    @Query("SELECT COALESCE(SUM(p.points), 0.0) FROM PointHistory p WHERE p.clanId = :clanId")
    double sumPointsByClanId(@Param("clanId") String clanId);

    @Query("SELECT COALESCE(SUM(p.points), 0.0) FROM PointHistory p " +
            "WHERE p.clanId = :clanId AND p.earnedAt >= :startDate AND p.earnedAt <= :endDate")
    double sumPointsByClanIdAndDateRange(
            @Param("clanId") String clanId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COALESCE(SUM(p.points), 0.0) FROM PointHistory p WHERE p.userId = :userId")
    double sumPointsByUserId(@Param("userId") String userId);

    @Query("SELECT p.userId, SUM(p.points) FROM PointHistory p WHERE p.userId IN :userIds GROUP BY p.userId")
    List<Object[]> sumPointsByUserIdsBulk(@Param("userIds") List<String> userIds);
}