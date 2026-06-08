package xyz.ianworley.keepsake.albums;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import xyz.ianworley.keepsake.media.PhotoSummary;

public record AlbumSummary(UUID id, UUID ownerId, String title, String description, Instant createdAt,
		Instant updatedAt, List<PhotoSummary> photos) {
}
