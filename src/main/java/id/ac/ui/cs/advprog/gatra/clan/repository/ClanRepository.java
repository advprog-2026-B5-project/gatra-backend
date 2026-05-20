package id.ac.ui.cs.advprog.gatra.clan.repository;

import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClanRepository extends JpaRepository<Clan, String> {
    boolean existsByName(String name);
    List<Clan> findByTier(String tier);
}
