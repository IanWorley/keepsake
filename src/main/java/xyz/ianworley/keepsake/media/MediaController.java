package xyz.ianworley.keepsake.media;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.ianworley.keepsake.identity.KeepsakeUserDetails;

@RestController
@RequestMapping("/api/photos")
class MediaController {
	private final MediaService media;

	MediaController(MediaService media) {
		this.media = media;
	}

	@GetMapping("/{id}")
	PhotoSummary get(@PathVariable UUID id) {
		return media.get(id);
	}

	@GetMapping("/{id}/file")
	ResponseEntity<Resource> file(@PathVariable UUID id) {
		MediaService.PhotoFile file = media.file(id);
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(file.photo().contentType()))
				.body(file.resource());
	}

	@PatchMapping("/{id}")
	PhotoSummary updateCaption(@PathVariable UUID id, @AuthenticationPrincipal KeepsakeUserDetails user,
			@Valid @RequestBody UpdatePhotoRequest request) {
		return media.updateCaption(id, user.id(), user.role().name().equals("ADMIN"), request.caption());
	}

	@DeleteMapping("/{id}")
	void delete(@PathVariable UUID id, @AuthenticationPrincipal KeepsakeUserDetails user) {
		media.delete(id, user.id(), user.role().name().equals("ADMIN"));
	}

	record UpdatePhotoRequest(String caption) {
	}
}
