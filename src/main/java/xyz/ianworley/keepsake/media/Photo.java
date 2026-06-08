package xyz.ianworley.keepsake.media;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Photo {
	@Id
	private UUID id;
	private UUID ownerId;
	@Enumerated(EnumType.STRING)
	private PhotoParentType parentType;
	private UUID parentId;
	private String caption;
	private String originalFilename;
	private String contentType;
	private long fileSize;
	private String storagePath;
	private Instant createdAt;
	private Instant updatedAt;
	private Instant deletedAt;

	protected Photo() {
	}

	public Photo(UUID ownerId, PhotoParentType parentType, UUID parentId, String caption,
			String originalFilename, String contentType, long fileSize, String storagePath) {
		this.id = UUID.randomUUID();
		this.ownerId = ownerId;
		this.parentType = parentType;
		this.parentId = parentId;
		this.caption = caption;
		this.originalFilename = originalFilename;
		this.contentType = contentType;
		this.fileSize = fileSize;
		this.storagePath = storagePath;
		this.createdAt = Instant.now();
		this.updatedAt = this.createdAt;
	}

	public UUID getId() { return id; }
	public UUID getOwnerId() { return ownerId; }
	public PhotoParentType getParentType() { return parentType; }
	public UUID getParentId() { return parentId; }
	public String getCaption() { return caption; }
	public String getOriginalFilename() { return originalFilename; }
	public String getContentType() { return contentType; }
	public long getFileSize() { return fileSize; }
	public String getStoragePath() { return storagePath; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
	public Instant getDeletedAt() { return deletedAt; }

	public void updateCaption(String caption) {
		this.caption = caption;
		this.updatedAt = Instant.now();
	}

	public void softDelete() {
		this.deletedAt = Instant.now();
		this.updatedAt = this.deletedAt;
	}
}
