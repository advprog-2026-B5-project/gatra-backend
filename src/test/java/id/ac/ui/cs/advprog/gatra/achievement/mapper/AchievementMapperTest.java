package id.ac.ui.cs.advprog.gatra.achievement.mapper;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;
import id.ac.ui.cs.advprog.gatra.achievement.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AchievementMapperTest {

    private AchievementMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AchievementMapper();
    }

    @Test
    void toResponse_success() {
        Achievement achievement = Achievement.builder()
                .id(UUID.randomUUID())
                .name("Test")
                .category(ActionType.FINISH_QUIZ)
                .milestoneThreshold(10)
                .description("Desc")
                .badgeUrl("url")
                .build();

        AchievementResponse response = mapper.toResponse(achievement);

        assertNotNull(response);
        assertEquals(achievement.getId(), response.getId());
        assertEquals("Test", response.getName());
    }

    @Test
    void toResponseFromUserAchievement_success() {
        Achievement achievement = Achievement.builder()
                .id(UUID.randomUUID())
                .name("Test")
                .build();
        UserAchievement ua = UserAchievement.builder()
                .id(UUID.randomUUID())
                .achievement(achievement)
                .unlockedAt(LocalDateTime.now())
                .isDisplayed(true)
                .build();

        AchievementResponse response = mapper.toResponseFromUserAchievement(ua);

        assertNotNull(response);
        assertEquals(achievement.getId(), response.getId());
        assertTrue(response.isDisplayed());
        assertNotNull(response.getUnlockedAt());
    }

    @Test
    void toEntity_success() {
        AchievementRequest req = new AchievementRequest();
        req.setName("Test");
        req.setCategory(ActionType.FINISH_QUIZ);
        req.setMilestoneThreshold(5);
        req.setDescription("Desc");
        req.setBadgeUrl("url");

        Achievement entity = mapper.toEntity(req);
        assertEquals("Test", entity.getName());
        assertEquals(ActionType.FINISH_QUIZ, entity.getCategory());
        assertEquals(5, entity.getMilestoneThreshold());
    }

    @Test
    void updateEntity_success() {
        Achievement achievement = new Achievement();
        AchievementRequest req = new AchievementRequest();
        req.setName("Updated");

        mapper.updateEntity(achievement, req);
        assertEquals("Updated", achievement.getName());
    }
}