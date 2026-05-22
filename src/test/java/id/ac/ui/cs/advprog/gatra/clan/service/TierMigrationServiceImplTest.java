package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TierMigrationServiceImplTest {

    @Mock
    private TierMigrationProcessor migrationProcessor;

    @InjectMocks
    private TierMigrationServiceImpl tierMigrationService;

    @Test
    void migrate_shouldCallProcessTierMigrationForEachLeaderboard() {
        TierLeaderboardResponse goldBoard = TierLeaderboardResponse.builder()
                .tier("GOLD").rankings(List.of()).build();
        TierLeaderboardResponse silverBoard = TierLeaderboardResponse.builder()
                .tier("SILVER").rankings(List.of()).build();

        tierMigrationService.migrate(List.of(goldBoard, silverBoard));

        verify(migrationProcessor, times(1)).processTierMigration(goldBoard);
        verify(migrationProcessor, times(1)).processTierMigration(silverBoard);
    }

    @Test
    void migrate_shouldNotCallProcessor_whenLeaderboardsEmpty() {
        tierMigrationService.migrate(List.of());

        verifyNoInteractions(migrationProcessor);
    }

    @Test
    void migrate_shouldCallProcessorOnce_whenSingleLeaderboard() {
        TierLeaderboardResponse bronzeBoard = TierLeaderboardResponse.builder()
                .tier("BRONZE").rankings(List.of()).build();

        tierMigrationService.migrate(List.of(bronzeBoard));

        verify(migrationProcessor, times(1)).processTierMigration(bronzeBoard);
    }
}