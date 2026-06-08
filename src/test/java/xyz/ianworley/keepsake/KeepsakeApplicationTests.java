package xyz.ianworley.keepsake;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class KeepsakeApplicationTests {

	@Test
	void hasApplicationEntryPoint() {
		assertThat(KeepsakeApplication.class).isNotNull();
	}

}
