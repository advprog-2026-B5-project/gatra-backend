package id.ac.ui.cs.advprog.gatra.achievement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyMissionRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Target count is required")
    @Min(value = 1, message = "Target count must be at least 1")
    private Integer targetCount;

    @NotNull(message = "Reward points is required")
    @Min(value = 0, message = "Reward points must be zero or positive")
    private Integer rewardPoints;

    @NotBlank(message = "Action type is required")
    private String actionType;

    @NotBlank(message = "Status is required")
    private String status;
}