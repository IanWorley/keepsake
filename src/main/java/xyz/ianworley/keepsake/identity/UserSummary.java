package xyz.ianworley.keepsake.identity;

import java.util.UUID;

public record UserSummary(UUID id, String username, String displayName, UserRole role) {
	static UserSummary from(AppUser user) {
		return new UserSummary(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole());
	}
}
