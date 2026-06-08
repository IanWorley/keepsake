package xyz.ianworley.keepsake.identity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
class AdminUsersController {
	private final UserService users;

	AdminUsersController(UserService users) {
		this.users = users;
	}

	@GetMapping
	List<UserSummary> list() {
		return users.list();
	}

	@PostMapping
	UserSummary create(@Valid @RequestBody CreateUserRequest request) {
		return users.create(request.username(), request.displayName(), request.password(), request.role());
	}

	record CreateUserRequest(@NotBlank @Size(max = 80) String username,
			@NotBlank @Size(max = 160) String displayName,
			@NotBlank @Size(min = 6, max = 120) String password,
			UserRole role) {
		CreateUserRequest {
			if (role == null) {
				role = UserRole.USER;
			}
		}
	}
}
