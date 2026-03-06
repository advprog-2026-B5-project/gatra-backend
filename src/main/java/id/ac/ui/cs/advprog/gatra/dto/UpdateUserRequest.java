package id.ac.ui.cs.advprog.gatra.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    // Validasi: Panjang nama minimal 3 karakter, maksimal 50 karakter
    @Size(min = 3, max = 50, message = "Display name harus antara 3 hingga 50 karakter")
    private String displayName;

    // Validasi Opsional: Hanya boleh berisi angka, minimal 10 digit, maksimal 15 digit
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Format nomor HP tidak valid (hanya angka, 10-15 digit)")
    private String phoneNumber;
}