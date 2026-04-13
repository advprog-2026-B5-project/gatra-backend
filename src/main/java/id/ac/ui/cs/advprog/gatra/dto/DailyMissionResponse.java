package id.ac.ui.cs.advprog.gatra.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyMissionResponse {
    private UUID id;
    private String title;
    private String description;
    private Integer targetCount;
    private String actionType;
    private boolean isActive;
}