package xyz.ianworley.keepsake.identity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_user")
public class AppUser {
	@Id
	private UUID id;
	private String username;
	private String displayName;
	private String passwordHash;
	@Enumerated(EnumType.STRING)
	private UserRole role;
	private Instant createdAt;
	private Instant updatedAt;
	private Instant deletedAt;

	protected AppUser() {
	}

	public AppUser(String username, String displayName, String passwordHash, UserRole role) {
		this.id = UUID.randomUUID();
		this.username = username;
		this.displayName = displayName;
		this.passwordHash = passwordHash;
		this.role = role;
		this.createdAt = Instant.now();
		this.updatedAt = this.createdAt;
	}

	public UUID getId() { return id; }
	public String getUsername() { return username; }
	public String getDisplayName() { return displayName; }
	public String getPasswordHash() { return passwordHash; }
	public UserRole getRole() { return role; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
	public Instant getDeletedAt() { return deletedAt; }
	public boolean isAdmin() { return role == UserRole.ADMIN; }

	public void update(String displayName, UserRole role) {
		this.displayName = displayName;
		this.role = role;
		this.updatedAt = Instant.now();
	}

	public void changePassword(String passwordHash) {
		this.passwordHash = passwordHash;
		this.updatedAt = Instant.now();
	}
}
