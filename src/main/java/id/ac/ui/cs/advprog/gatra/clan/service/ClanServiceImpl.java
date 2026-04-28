package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.*;
import id.ac.ui.cs.advprog.gatra.clan.model.*;
import id.ac.ui.cs.advprog.gatra.clan.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClanServiceImpl implements ClanService {
    private final ClanRepository clanRepository;
    private final ClanMembershipRepository membershipRepository;

    @Override
    @Transactional
    public ClanResponse createClan(CreateClanRequest request, String userId){
        if(membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.APPROVED) ||
            membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.PENDING)){
            throw new RuntimeException("User sudah terdaftar atau memiliki pending di sebuah clan.");
        }

        Clan clan=Clan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        clanRepository.save(clan);

        ClanMembership membership = ClanMembership.builder()
                .clan(clan)
                .userId(userId)
                .role(ClanRole.LEADER)
                .status(MembershipStatus.APPROVED)
                .build();
        membershipRepository.save(membership);

        return toResponse(clan);
    }

    private ClanResponse toResponse(Clan clan){
        return ClanResponse.builder()
                .id(clan.getId())
                .name(clan.getName())
                .description(clan.getDescription())
                .createdAt(clan.getCreatedAt())
                .build();
    }

    @Override
    public ClanResponse getClan(String clanId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan dengan id " + clanId + " tidak ditemukan"));
        return toResponse(clan);
    }

    @Override
    @Transactional
    public void deleteClan(String clanId, String userId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan dengan id " + clanId + " tidak ditemukan"));

        // hanya leader yg bisa hapus
        membershipRepository.findByClanIdAndUserId(clanId, userId)
                .filter(m -> m.getRole() == ClanRole.LEADER)
                .orElseThrow(() -> new RuntimeException("Hanya ketua clan yang dapat menghapus clan"));

        clanRepository.delete(clan);
    }


}
