package xyz.ianworley.keepsake.comments;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentsService {
	private final CommentRepository comments;

	CommentsService(CommentRepository comments) {
		this.comments = comments;
	}

	@Transactional
	public CommentSummary add(UUID ownerId, CommentTargetType targetType, UUID targetId, String body) {
		return CommentSummary.from(comments.save(new Comment(ownerId, targetType, targetId, requireBody(body))));
	}

	@Transactional(readOnly = true)
	public List<CommentSummary> findFor(CommentTargetType targetType, UUID targetId) {
		return comments.findByTargetTypeAndTargetIdAndDeletedAtIsNullOrderByCreatedAtAsc(targetType, targetId)
				.stream().map(CommentSummary::from).toList();
	}

	@Transactional
	public CommentSummary update(UUID id, UUID actorId, boolean admin, String body) {
		Comment comment = comments.findByIdAndDeletedAtIsNull(id).orElseThrow();
		requireOwnerOrAdmin(comment.getOwnerId(), actorId, admin);
		comment.updateBody(requireBody(body));
		return CommentSummary.from(comment);
	}

	@Transactional
	public void delete(UUID id, UUID actorId, boolean admin) {
		Comment comment = comments.findByIdAndDeletedAtIsNull(id).orElseThrow();
		requireOwnerOrAdmin(comment.getOwnerId(), actorId, admin);
		comment.softDelete();
	}

	private String requireBody(String body) {
		if (body == null || body.isBlank()) {
			throw new IllegalArgumentException("Comment body is required");
		}
		return body.strip();
	}

	private void requireOwnerOrAdmin(UUID ownerId, UUID actorId, boolean admin) {
		if (!admin && !ownerId.equals(actorId)) {
			throw new IllegalStateException("You do not have permission to change this comment");
		}
	}
}
