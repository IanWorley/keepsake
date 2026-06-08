package xyz.ianworley.keepsake.media;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keepsake.storage")
public record StorageProperties(String localRoot) {
}
