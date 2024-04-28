package main.proj.social.feed.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import main.proj.social.feed.like.LikeService;
import main.proj.social.fileManagement.exceptions.StorageException;
import main.proj.social.user.UserRepository;
import main.proj.social.user.dto.UserPublicDto;
import main.proj.social.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "Posts")
public class PostController {
    private final PostService postService;
    private final UserRepository userRepository;
    private final LikeService likeService;
    private final PostRepository postRepository;

    @Operation(summary = "Get news feed for User")
    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getNewsFeed(Principal principal) {
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Post> feed = postService.getNewsFeedForUser(user.getId());
        return ResponseEntity.ok(feed);
    }

    @Operation(summary = "Get post")
    @GetMapping("{postId}")
    public ResponseEntity<Post> GetPost(@PathVariable Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return ResponseEntity.ok(post);
    }
    @Operation(summary = "Create new post")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Post> createPost(@RequestPart("postRequest") PostRequest postRequest,
                                           @RequestPart("photos") List<MultipartFile> photos,
                                           Principal principal) {
        try {
            Post post = postService.createPost(principal.getName(), postRequest, photos);
            return ResponseEntity.ok(post);
        } catch (StorageException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Operation(summary = "Edit post")
    @PutMapping(value = "/{postId}", consumes = "multipart/form-data")
    public ResponseEntity<Post> editPost(@PathVariable Long postId,
                                         @RequestPart("newContent") String newContent,
                                         @RequestPart("photos") List<MultipartFile> newPhotos,
                                         Principal principal) {
        try {
            Post post = postService.editPost(postId, principal.getName(), newContent, newPhotos);
            return ResponseEntity.ok(post);
        } catch (StorageException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Operation(summary = "Delete post")
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, Principal principal) {
        postService.deletePost(postId, principal.getName());
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "Get user's posts")
    @GetMapping("/user/{username}")
    public ResponseEntity<List<Post>> getPostsByUser(@PathVariable String username) {
        List<Post> posts = postService.getPostsByUser(username);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "Get post's like list(users who liked posts)")
    @GetMapping("/{postId}/likes")
    public ResponseEntity<List<UserPublicDto>> getUsersWhoLikedPost(@PathVariable Long postId) {
        List<UserPublicDto> users = likeService.getUsersWhoLikedPost(postId);
        return ResponseEntity.ok(users);
    }
    @Operation(summary = "Get user's liked posts")
    @GetMapping("/liked")
    public ResponseEntity<List<Post>> getLikedPosts(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Post> posts = postService.getLikedPosts(user.getId());
        return ResponseEntity.ok(posts);
    }

}
