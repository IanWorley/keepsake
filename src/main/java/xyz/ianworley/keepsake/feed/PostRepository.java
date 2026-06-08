package xyz.ianworley.keepsake.feed;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface PostRepository extends JpaRepository<Post, UUID> {
	List<Post> findByDeletedAtIsNullOrderByCreatedAtDesc();
	Optional<Post> findByIdAndDeletedAtIsNull(UUID id);
}
