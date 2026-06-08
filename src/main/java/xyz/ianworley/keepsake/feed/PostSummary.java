package xyz.ianworley.keepsake.feed;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import xyz.ianworley.keepsake.comments.CommentSummary;
import xyz.ianworley.keepsake.media.PhotoSummary;

public record PostSummary(UUID id, UUID ownerId, String body, Instant createdAt, Instant updatedAt,
		List<PhotoSummary> photos, List<CommentSummary> comments) {
}
