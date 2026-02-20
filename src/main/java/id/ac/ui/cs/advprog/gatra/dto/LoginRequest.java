package id.ac.ui.cs.advprog.gatra.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email atau Nomor HP harus diisi")
    private String identifier; // Bisa berisi email ATAU nomor HP

    @NotBlank(message = "Password tidak boleh kosong")
    private String password;
}
