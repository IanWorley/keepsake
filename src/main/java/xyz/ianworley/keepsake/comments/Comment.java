package xyz.ianworley.keepsake.comments;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "comment")
public class Comment {
	@Id
	private UUID id;
	private UUID ownerId;
	@Enumerated(EnumType.STRING)
	private CommentTargetType targetType;
	private UUID targetId;
	private String body;
	private Instant createdAt;
	private Instant updatedAt;
	private Instant deletedAt;

	protected Comment() {
	}

	public Comment(UUID ownerId, CommentTargetType targetType, UUID targetId, String body) {
		this.id = UUID.randomUUID();
		this.ownerId = ownerId;
		this.targetType = targetType;
		this.targetId = targetId;
		this.body = body;
		this.createdAt = Instant.now();
		this.updatedAt = this.createdAt;
	}

	public UUID getId() { return id; }
	public UUID getOwnerId() { return ownerId; }
	public CommentTargetType getTargetType() { return targetType; }
	public UUID getTargetId() { return targetId; }
	public String getBody() { return body; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
	public Instant getDeletedAt() { return deletedAt; }

	public void updateBody(String body) {
		this.body = body;
		this.updatedAt = Instant.now();
	}

	public void softDelete() {
		this.deletedAt = Instant.now();
		this.updatedAt = this.deletedAt;
	}
}
