package xyz.ianworley.keepsake;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ApplicationModuleTests {
	@Test
	void verifiesModularStructure() {
		ApplicationModules.of(KeepsakeApplication.class).verify();
	}
}
