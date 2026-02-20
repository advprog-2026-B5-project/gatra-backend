package id.ac.ui.cs.advprog.gatra.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token; // JWT Token untuk disimpan di localStorage React
    private String userId;
    private String username;
    private String role;
}
