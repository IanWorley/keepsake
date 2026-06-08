package xyz.ianworley.keepsake.media;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface PhotoRepository extends JpaRepository<Photo, UUID> {
	List<Photo> findByParentTypeAndParentIdAndDeletedAtIsNullOrderByCreatedAtAsc(PhotoParentType parentType, UUID parentId);
	Optional<Photo> findByIdAndDeletedAtIsNull(UUID id);
}
