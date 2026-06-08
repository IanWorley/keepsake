package xyz.ianworley.keepsake;

import org.springframework.boot.SpringApplication;

public class TestKeepsakeApplication {

	public static void main(String[] args) {
		SpringApplication.from(KeepsakeApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
