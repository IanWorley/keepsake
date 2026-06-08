package xyz.ianworley.keepsake.albums;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface AlbumRepository extends JpaRepository<Album, UUID> {
	List<Album> findByDeletedAtIsNullOrderByCreatedAtDesc();
	Optional<Album> findByIdAndDeletedAtIsNull(UUID id);
}
