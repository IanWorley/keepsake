package xyz.ianworley.keepsake.comments;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.ianworley.keepsake.identity.KeepsakeUserDetails;

@RestController
@RequestMapping("/api/comments")
class CommentsController {
	private final CommentsService comments;

	CommentsController(CommentsService comments) {
		this.comments = comments;
	}

	@GetMapping("/{targetType}/{targetId}")
	List<CommentSummary> list(@PathVariable CommentTargetType targetType, @PathVariable UUID targetId) {
		return comments.findFor(targetType, targetId);
	}

	@PostMapping("/{targetType}/{targetId}")
	CommentSummary add(@PathVariable CommentTargetType targetType, @PathVariable UUID targetId,
			@AuthenticationPrincipal KeepsakeUserDetails user, @Valid @RequestBody CommentRequest request) {
		return comments.add(user.id(), targetType, targetId, request.body());
	}

	@PatchMapping("/{id}")
	CommentSummary update(@PathVariable UUID id, @AuthenticationPrincipal KeepsakeUserDetails user,
			@Valid @RequestBody CommentRequest request) {
		return comments.update(id, user.id(), user.role().name().equals("ADMIN"), request.body());
	}

	@DeleteMapping("/{id}")
	void delete(@PathVariable UUID id, @AuthenticationPrincipal KeepsakeUserDetails user) {
		comments.delete(id, user.id(), user.role().name().equals("ADMIN"));
	}

	record CommentRequest(@NotBlank String body) {
	}
}
