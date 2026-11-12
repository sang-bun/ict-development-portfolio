package umc.fitty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.fitty.domain.RunRecord;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository extends JpaRepository<RunRecord,Long> {
    List<RunRecord> findAllByGoalIdAndCreatedAtBetween(Long goalId,
                                                        LocalDateTime start,
                                                        LocalDateTime end);
}
