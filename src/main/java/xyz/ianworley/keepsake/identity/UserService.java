package xyz.ianworley.keepsake.identity;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
	private final AppUserRepository users;
	private final PasswordEncoder passwordEncoder;
	private final String seedAdminUsername;
	private final String seedAdminPassword;

	UserService(AppUserRepository users, PasswordEncoder passwordEncoder,
			@Value("${keepsake.security.seed-admin-username}") String seedAdminUsername,
			@Value("${keepsake.security.seed-admin-password}") String seedAdminPassword) {
		this.users = users;
		this.passwordEncoder = passwordEncoder;
		this.seedAdminUsername = seedAdminUsername;
		this.seedAdminPassword = seedAdminPassword;
	}

	@Transactional
	public void seedAdmin() {
		if (!users.existsByUsernameIgnoreCaseAndDeletedAtIsNull(seedAdminUsername)) {
			users.save(new AppUser(seedAdminUsername, "Keepsake Admin",
					passwordEncoder.encode(seedAdminPassword), UserRole.ADMIN));
		}
	}

	@Transactional(readOnly = true)
	public KeepsakeUserDetails loadDetails(String username) {
		AppUser user = users.findByUsernameIgnoreCaseAndDeletedAtIsNull(username)
				.orElseThrow(() -> new IllegalArgumentException("Unknown user"));
		return new KeepsakeUserDetails(user.getId(), user.getUsername(), user.getDisplayName(),
				user.getPasswordHash(), user.getRole());
	}

	@Transactional
	public UserSummary create(String username, String displayName, String password, UserRole role) {
		if (users.existsByUsernameIgnoreCaseAndDeletedAtIsNull(username)) {
			throw new IllegalArgumentException("Username is already taken");
		}
		return UserSummary.from(users.save(new AppUser(username, displayName,
				passwordEncoder.encode(password), role)));
	}

	@Transactional(readOnly = true)
	public List<UserSummary> list() {
		return users.findByDeletedAtIsNullOrderByCreatedAtAsc().stream().map(UserSummary::from).toList();
	}

	@Transactional(readOnly = true)
	public UserSummary get(UUID id) {
		return UserSummary.from(users.findById(id).orElseThrow());
	}
}
