package id.ac.ui.cs.advprog.gatra.clan.repository;

import id.ac.ui.cs.advprog.gatra.clan.model.SeasonSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeasonSnapshotRepository extends JpaRepository<SeasonSnapshot, String> {
    List<SeasonSnapshot> findBySeasonNumberOrderByTierAscFinalRankAsc(int seasonNumber);
    int countDistinctBySeasonNumber(int seasonNumber); // untuk auto-increment season number
}