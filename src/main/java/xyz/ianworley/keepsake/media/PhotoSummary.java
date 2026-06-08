package xyz.ianworley.keepsake.media;

import java.time.Instant;
import java.util.UUID;

public record PhotoSummary(UUID id, UUID ownerId, PhotoParentType parentType, UUID parentId, String caption,
		String originalFilename, String contentType, long fileSize, Instant createdAt, Instant updatedAt) {
	static PhotoSummary from(Photo photo) {
		return new PhotoSummary(photo.getId(), photo.getOwnerId(), photo.getParentType(), photo.getParentId(),
				photo.getCaption(), photo.getOriginalFilename(), photo.getContentType(), photo.getFileSize(),
				photo.getCreatedAt(), photo.getUpdatedAt());
	}
}
