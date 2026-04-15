package id.ac.ui.cs.advprog.gatra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyMissionRequest {
    private String title;
    private String description;
    private Integer targetCount;
    private Integer rewardPoints;
    private String actionType;
    private String status;
}