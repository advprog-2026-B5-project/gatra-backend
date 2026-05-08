package id.ac.ui.cs.advprog.gatra.clan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateClanRequest {

    @NotBlank(message = "Nama clan tidak boleh kosong")
    private String name;
    private String description;
}
