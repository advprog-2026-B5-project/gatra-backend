package id.ac.ui.cs.advprog.gatra.achievement.dto;

import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AchievementRequest {
    @NotBlank(message = "Nama achievement tidak boleh kosong.")
    private String name;

    @NotNull(message = "Category tidak boleh kosong.")
    private ActionType category;

    @NotNull(message = "Milestone threshold tidak boleh kosong.")
    @Min(value = 1, message = "Milestone threshold harus minimal 1.")
    private Integer milestoneThreshold;

    private String description;

    private String badgeUrl;
}