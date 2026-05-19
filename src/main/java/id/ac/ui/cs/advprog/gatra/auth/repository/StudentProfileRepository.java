package id.ac.ui.cs.advprog.gatra.auth.repository;

import id.ac.ui.cs.advprog.gatra.auth.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, UUID> {
    @Modifying
    @Query("UPDATE StudentProfile s SET s.totalScore = 0")
    void resetAllStudentPoints();
}
