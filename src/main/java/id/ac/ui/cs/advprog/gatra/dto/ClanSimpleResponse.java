package id.ac.ui.cs.advprog.gatra.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClanSimpleResponse {
    private String id;
    private String name;
    private String role;
}