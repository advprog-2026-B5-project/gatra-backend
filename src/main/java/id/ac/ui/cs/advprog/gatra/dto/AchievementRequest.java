package id.ac.ui.cs.advprog.gatra.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AchievementRequest {
    @NotBlank(message = "Nama achievement tidak boleh kosong.")
    private String name;

    private String category;

    @NotNull(message = "Milestone threshold tidak boleh kosong.")
    @Min(value = 1, message = "Milestone threshold harus minimal 1.")
    private Integer milestoneThreshold;

    private String description;

    private String badgeUrl;
}