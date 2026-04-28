package id.ac.ui.cs.advprog.gatra.clan.dto;

import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MembershipDecisionRequest {

    @NotNull
    private MembershipStatus decision;
}
