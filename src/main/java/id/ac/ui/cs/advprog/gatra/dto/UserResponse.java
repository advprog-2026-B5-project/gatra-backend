package id.ac.ui.cs.advprog.gatra.dto; // Sesuaikan dengan package Anda

import id.ac.ui.cs.advprog.gatra.model.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String phoneNumber;
    private String displayName;
    private Role role;
}