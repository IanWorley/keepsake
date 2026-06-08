package xyz.ianworley.keepsake.albums;

import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import xyz.ianworley.keepsake.media.MediaService;
import xyz.ianworley.keepsake.media.PhotoParentType;
import xyz.ianworley.keepsake.media.PhotoSummary;

@Service
public class AlbumService {
	private final AlbumRepository albums;
	private final MediaService media;
	private final ApplicationEventPublisher events;

	AlbumService(AlbumRepository albums, MediaService media, ApplicationEventPublisher events) {
		this.albums = albums;
		this.media = media;
		this.events = events;
	}

	@Transactional
	public AlbumSummary create(UUID ownerId, String title, String description) {
		Album album = albums.save(new Album(ownerId, title, description));
		events.publishEvent(new AlbumCreated(album.getId(), ownerId));
		return summary(album);
	}

	@Transactional(readOnly = true)
	public List<AlbumSummary> list() {
		return albums.findByDeletedAtIsNullOrderByCreatedAtDesc().stream().map(this::summary).toList();
	}

	@Transactional(readOnly = true)
	public AlbumSummary get(UUID id) {
		return summary(albums.findByIdAndDeletedAtIsNull(id).orElseThrow());
	}

	@Transactional
	public AlbumSummary update(UUID id, UUID actorId, boolean admin, String title, String description) {
		Album album = albums.findByIdAndDeletedAtIsNull(id).orElseThrow();
		requireOwnerOrAdmin(album.getOwnerId(), actorId, admin);
		album.update(title, description);
		return summary(album);
	}

	@Transactional
	public void delete(UUID id, UUID actorId, boolean admin) {
		Album album = albums.findByIdAndDeletedAtIsNull(id).orElseThrow();
		requireOwnerOrAdmin(album.getOwnerId(), actorId, admin);
		album.softDelete();
	}

	@Transactional
	public List<PhotoSummary> uploadPhotos(UUID albumId, UUID ownerId, List<MultipartFile> files) {
		albums.findByIdAndDeletedAtIsNull(albumId).orElseThrow();
		if (files == null || files.isEmpty()) {
			throw new IllegalArgumentException("At least one photo is required");
		}
		return files.stream().filter(file -> file != null && !file.isEmpty())
				.map(file -> media.store(ownerId, PhotoParentType.ALBUM, albumId, null, file)).toList();
	}

	private AlbumSummary summary(Album album) {
		return new AlbumSummary(album.getId(), album.getOwnerId(), album.getTitle(), album.getDescription(),
				album.getCreatedAt(), album.getUpdatedAt(),
				media.findByParent(PhotoParentType.ALBUM, album.getId()));
	}

	private void requireOwnerOrAdmin(UUID ownerId, UUID actorId, boolean admin) {
		if (!admin && !ownerId.equals(actorId)) {
			throw new IllegalStateException("You do not have permission to change this album");
		}
	}

	public record AlbumCreated(UUID albumId, UUID ownerId) {
	}
}
