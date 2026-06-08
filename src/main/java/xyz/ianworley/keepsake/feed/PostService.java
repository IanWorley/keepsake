package xyz.ianworley.keepsake.feed;

import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import xyz.ianworley.keepsake.comments.CommentSummary;
import xyz.ianworley.keepsake.comments.CommentTargetType;
import xyz.ianworley.keepsake.comments.CommentsService;
import xyz.ianworley.keepsake.media.MediaService;
import xyz.ianworley.keepsake.media.PhotoParentType;
import xyz.ianworley.keepsake.media.PhotoSummary;

@Service
public class PostService {
	private final PostRepository posts;
	private final MediaService media;
	private final CommentsService comments;
	private final ApplicationEventPublisher events;

	PostService(PostRepository posts, MediaService media, CommentsService comments,
			ApplicationEventPublisher events) {
		this.posts = posts;
		this.media = media;
		this.comments = comments;
		this.events = events;
	}

	@Transactional
	public PostSummary create(UUID ownerId, String body, List<MultipartFile> files) {
		Post post = posts.save(new Post(ownerId, body));
		if (files != null) {
			files.stream().filter(file -> file != null && !file.isEmpty())
					.forEach(file -> media.store(ownerId, PhotoParentType.POST, post.getId(), null, file));
		}
		events.publishEvent(new PostCreated(post.getId(), ownerId));
		return summary(post);
	}

	@Transactional(readOnly = true)
	public List<PostSummary> feed() {
		return posts.findByDeletedAtIsNullOrderByCreatedAtDesc().stream().map(this::summary).toList();
	}

	@Transactional
	public PostSummary update(UUID id, UUID actorId, boolean admin, String body) {
		Post post = posts.findByIdAndDeletedAtIsNull(id).orElseThrow();
		requireOwnerOrAdmin(post.getOwnerId(), actorId, admin);
		post.updateBody(body);
		return summary(post);
	}

	@Transactional
	public void delete(UUID id, UUID actorId, boolean admin) {
		Post post = posts.findByIdAndDeletedAtIsNull(id).orElseThrow();
		requireOwnerOrAdmin(post.getOwnerId(), actorId, admin);
		post.softDelete();
	}

	private PostSummary summary(Post post) {
		List<PhotoSummary> postPhotos = media.findByParent(PhotoParentType.POST, post.getId());
		List<CommentSummary> postComments = comments.findFor(CommentTargetType.POST, post.getId());
		return new PostSummary(post.getId(), post.getOwnerId(), post.getBody(), post.getCreatedAt(),
				post.getUpdatedAt(), postPhotos, postComments);
	}

	private void requireOwnerOrAdmin(UUID ownerId, UUID actorId, boolean admin) {
		if (!admin && !ownerId.equals(actorId)) {
			throw new IllegalStateException("You do not have permission to change this post");
		}
	}
}
