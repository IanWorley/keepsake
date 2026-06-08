package xyz.ianworley.keepsake.feed;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Post {
	@Id
	private UUID id;
	private UUID ownerId;
	private String body;
	private Instant createdAt;
	private Instant updatedAt;
	private Instant deletedAt;

	protected Post() {
	}

	public Post(UUID ownerId, String body) {
		this.id = UUID.randomUUID();
		this.ownerId = ownerId;
		this.body = body == null ? "" : body.strip();
		this.createdAt = Instant.now();
		this.updatedAt = this.createdAt;
	}

	public UUID getId() { return id; }
	public UUID getOwnerId() { return ownerId; }
	public String getBody() { return body; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
	public Instant getDeletedAt() { return deletedAt; }

	public void updateBody(String body) {
		this.body = body == null ? "" : body.strip();
		this.updatedAt = Instant.now();
	}

	public void softDelete() {
		this.deletedAt = Instant.now();
		this.updatedAt = this.deletedAt;
	}
}
