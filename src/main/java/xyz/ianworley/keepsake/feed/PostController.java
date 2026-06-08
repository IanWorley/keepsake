package xyz.ianworley.keepsake.feed;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.ianworley.keepsake.identity.KeepsakeUserDetails;

@RestController
@RequestMapping("/api/posts")
class PostController {
	private final PostService posts;

	PostController(PostService posts) {
		this.posts = posts;
	}

	@GetMapping
	List<PostSummary> feed() {
		return posts.feed();
	}

	@PostMapping(consumes = "multipart/form-data")
	PostSummary create(@AuthenticationPrincipal KeepsakeUserDetails user,
			@RequestParam(name = "body", required = false) String body,
			@RequestPart(name = "files", required = false) List<MultipartFile> files) {
		return posts.create(user.id(), body, files);
	}

	@PatchMapping("/{id}")
	PostSummary update(@PathVariable UUID id, @AuthenticationPrincipal KeepsakeUserDetails user,
			@Valid @RequestBody PostRequest request) {
		return posts.update(id, user.id(), user.role().name().equals("ADMIN"), request.body());
	}

	@DeleteMapping("/{id}")
	void delete(@PathVariable UUID id, @AuthenticationPrincipal KeepsakeUserDetails user) {
		posts.delete(id, user.id(), user.role().name().equals("ADMIN"));
	}

	record PostRequest(String body) {
	}
}
