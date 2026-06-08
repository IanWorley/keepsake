package xyz.ianworley.keepsake.feed;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class PostTests {
	@Test
	void trimsBodyAndTracksEdits() {
		Post post = new Post(UUID.randomUUID(), "  first note  ");

		assertThat(post.getBody()).isEqualTo("first note");

		post.updateBody("  edited note  ");

		assertThat(post.getBody()).isEqualTo("edited note");
		assertThat(post.getUpdatedAt()).isAfterOrEqualTo(post.getCreatedAt());
	}
}
