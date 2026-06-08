package xyz.ianworley.keepsake.feed;

import java.util.UUID;

public record PostCreated(UUID postId, UUID ownerId) {
}
