package umc.fitty.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import umc.fitty.domain.Comment;
import umc.fitty.domain.RunRecord;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    Page<Comment> findByRecord(RunRecord runRecord, Pageable pageable);

}
