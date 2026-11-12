package umc.fitty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.fitty.domain.Goal;

import java.time.LocalDate;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    Optional<Goal> findByUserIdAndWeekStartDate(Long userId, LocalDate weekStartDate);
}