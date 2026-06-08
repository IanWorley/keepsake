package xyz.ianworley.keepsake.albums;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Album {
	@Id
	private UUID id;
	private UUID ownerId;
	private String title;
	private String description;
	private Instant createdAt;
	private Instant updatedAt;
	private Instant deletedAt;

	protected Album() {
	}

	public Album(UUID ownerId, String title, String description) {
		this.id = UUID.randomUUID();
		this.ownerId = ownerId;
		this.title = requireTitle(title);
		this.description = blankToNull(description);
		this.createdAt = Instant.now();
		this.updatedAt = this.createdAt;
	}

	public UUID getId() { return id; }
	public UUID getOwnerId() { return ownerId; }
	public String getTitle() { return title; }
	public String getDescription() { return description; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
	public Instant getDeletedAt() { return deletedAt; }

	public void update(String title, String description) {
		this.title = requireTitle(title);
		this.description = blankToNull(description);
		this.updatedAt = Instant.now();
	}

	public void softDelete() {
		this.deletedAt = Instant.now();
		this.updatedAt = this.deletedAt;
	}

	private String requireTitle(String title) {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("Album title is required");
		}
		return title.strip();
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value.strip();
	}
}
