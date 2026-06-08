package xyz.ianworley.keepsake.albums;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.ianworley.keepsake.identity.KeepsakeUserDetails;
import xyz.ianworley.keepsake.media.PhotoSummary;

@RestController
@RequestMapping("/api/albums")
class AlbumController {
	private final AlbumService albums;

	AlbumController(AlbumService albums) {
		this.albums = albums;
	}

	@GetMapping
	List<AlbumSummary> list() {
		return albums.list();
	}

	@GetMapping("/{id}")
	AlbumSummary get(@PathVariable UUID id) {
		return albums.get(id);
	}

	@PostMapping
	AlbumSummary create(@AuthenticationPrincipal KeepsakeUserDetails user,
			@Valid @RequestBody AlbumRequest request) {
		return albums.create(user.id(), request.title(), request.description());
	}

	@PatchMapping("/{id}")
	AlbumSummary update(@PathVariable UUID id, @AuthenticationPrincipal KeepsakeUserDetails user,
			@Valid @RequestBody AlbumRequest request) {
		return albums.update(id, user.id(), user.role().name().equals("ADMIN"), request.title(), request.description());
	}

	@DeleteMapping("/{id}")
	void delete(@PathVariable UUID id, @AuthenticationPrincipal KeepsakeUserDetails user) {
		albums.delete(id, user.id(), user.role().name().equals("ADMIN"));
	}

	@PostMapping(value = "/{id}/photos", consumes = "multipart/form-data")
	List<PhotoSummary> upload(@PathVariable UUID id, @AuthenticationPrincipal KeepsakeUserDetails user,
			@RequestPart(name = "files") List<MultipartFile> files) {
		return albums.uploadPhotos(id, user.id(), files);
	}

	record AlbumRequest(@NotBlank String title, String description) {
	}
}
