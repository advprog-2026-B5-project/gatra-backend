package id.ac.ui.cs.advprog.gatra.clan.repository;

import id.ac.ui.cs.advprog.gatra.clan.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClanMembershipRepository extends JpaRepository<ClanMembership, String> {

    boolean existsByUserIdAndStatus(String userId, MembershipStatus status);
    Optional<ClanMembership> findByClanIdAndUserId(String clanId, String userId);
    List<ClanMembership> findByClanIdAndStatus(String clanId, MembershipStatus status);
    Optional<ClanMembership> findByUserIdAndStatus(String userId, MembershipStatus status);
    long countByClanIdAndStatus(String clanId, MembershipStatus status);
    Optional<ClanMembership> findFirstByUserIdAndStatus(String userId, MembershipStatus status);
}