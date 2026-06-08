package xyz.ianworley.keepsake.identity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface AppUserRepository extends JpaRepository<AppUser, UUID> {
	Optional<AppUser> findByUsernameIgnoreCaseAndDeletedAtIsNull(String username);
	boolean existsByUsernameIgnoreCaseAndDeletedAtIsNull(String username);
	List<AppUser> findByDeletedAtIsNullOrderByCreatedAtAsc();
}
