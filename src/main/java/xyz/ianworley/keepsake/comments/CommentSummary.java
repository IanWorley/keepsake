package xyz.ianworley.keepsake.comments;

import java.time.Instant;
import java.util.UUID;

public record CommentSummary(UUID id, UUID ownerId, CommentTargetType targetType, UUID targetId, String body,
		Instant createdAt, Instant updatedAt, boolean edited) {
	static CommentSummary from(Comment comment) {
		return new CommentSummary(comment.getId(), comment.getOwnerId(), comment.getTargetType(),
				comment.getTargetId(), comment.getBody(), comment.getCreatedAt(), comment.getUpdatedAt(),
				comment.getUpdatedAt().isAfter(comment.getCreatedAt()));
	}
}
