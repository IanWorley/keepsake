package xyz.ianworley.keepsake.comments;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface CommentRepository extends JpaRepository<Comment, UUID> {
	List<Comment> findByTargetTypeAndTargetIdAndDeletedAtIsNullOrderByCreatedAtAsc(CommentTargetType targetType, UUID targetId);
	Optional<Comment> findByIdAndDeletedAtIsNull(UUID id);
}
