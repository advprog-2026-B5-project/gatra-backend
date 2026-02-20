package id.ac.ui.cs.advprog.gatra.repository;

import id.ac.ui.cs.advprog.gatra.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);

    // [Custom Query] Untuk fitur login fleksibel (Email ATAU No HP)
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.phoneNumber = :identifier OR u.username = :identifier")
    Optional<User> findByUserIdentifier(String identifier);
}
