package xyz.ianworley.keepsake.media;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@EnableConfigurationProperties(StorageProperties.class)
public class MediaService {
	private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

	private final PhotoRepository photos;
	private final Path root;

	MediaService(PhotoRepository photos, StorageProperties properties) {
		this.photos = photos;
		this.root = Path.of(properties.localRoot()).toAbsolutePath().normalize();
	}

	@Transactional
	public PhotoSummary store(UUID ownerId, PhotoParentType parentType, UUID parentId, String caption,
			MultipartFile file) {
		validate(file);
		try {
			Files.createDirectories(root);
			String extension = extension(file.getOriginalFilename());
			String storedName = UUID.randomUUID() + extension;
			Path destination = root.resolve(storedName).normalize();
			try (InputStream input = file.getInputStream()) {
				Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
			}
			Photo photo = new Photo(ownerId, parentType, parentId, blankToNull(caption),
					StringUtils.cleanPath(file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename()),
					file.getContentType(), file.getSize(), storedName);
			return PhotoSummary.from(photos.save(photo));
		}
		catch (IOException ex) {
			throw new IllegalStateException("Could not store uploaded photo", ex);
		}
	}

	@Transactional(readOnly = true)
	public List<PhotoSummary> findByParent(PhotoParentType parentType, UUID parentId) {
		return photos.findByParentTypeAndParentIdAndDeletedAtIsNullOrderByCreatedAtAsc(parentType, parentId)
				.stream().map(PhotoSummary::from).toList();
	}

	@Transactional(readOnly = true)
	public PhotoSummary get(UUID id) {
		return PhotoSummary.from(photos.findByIdAndDeletedAtIsNull(id).orElseThrow());
	}

	@Transactional(readOnly = true)
	public PhotoFile file(UUID id) {
		Photo photo = photos.findByIdAndDeletedAtIsNull(id).orElseThrow();
		try {
			Resource resource = new UrlResource(root.resolve(photo.getStoragePath()).normalize().toUri());
			return new PhotoFile(PhotoSummary.from(photo), resource);
		}
		catch (Exception ex) {
			throw new IllegalStateException("Could not read photo", ex);
		}
	}

	@Transactional
	public PhotoSummary updateCaption(UUID id, UUID actorId, boolean admin, String caption) {
		Photo photo = photos.findByIdAndDeletedAtIsNull(id).orElseThrow();
		requireOwnerOrAdmin(photo.getOwnerId(), actorId, admin);
		photo.updateCaption(blankToNull(caption));
		return PhotoSummary.from(photo);
	}

	@Transactional
	public void delete(UUID id, UUID actorId, boolean admin) {
		Photo photo = photos.findByIdAndDeletedAtIsNull(id).orElseThrow();
		requireOwnerOrAdmin(photo.getOwnerId(), actorId, admin);
		photo.softDelete();
	}

	private void validate(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("A photo file is required");
		}
		if (!ALLOWED_TYPES.contains(file.getContentType())) {
			throw new IllegalArgumentException("Only JPEG, PNG, and WebP images are supported");
		}
	}

	private String extension(String filename) {
		String clean = filename == null ? "" : StringUtils.cleanPath(filename);
		int dot = clean.lastIndexOf('.');
		return dot >= 0 ? clean.substring(dot).toLowerCase() : "";
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value;
	}

	private void requireOwnerOrAdmin(UUID ownerId, UUID actorId, boolean admin) {
		if (!admin && !ownerId.equals(actorId)) {
			throw new IllegalStateException("You do not have permission to change this photo");
		}
	}

	public record PhotoFile(PhotoSummary photo, Resource resource) {
	}
}
