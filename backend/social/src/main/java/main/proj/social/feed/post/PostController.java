package main.proj.social.feed.post;

import lombok.RequiredArgsConstructor;
import main.proj.social.feed.like.LikeService;
import main.proj.social.user.UserRepository;
import main.proj.social.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserRepository userRepository;
    private final LikeService likeService;

    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getNewsFeed(Principal principal) {
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Post> feed = postService.getNewsFeedForUser(user.getId());
        return ResponseEntity.ok(feed);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody String content, Principal principal) {
        Post post = postService.createPost(principal.getName(), content);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> editPost(@PathVariable Long postId, @RequestBody String newContent, Principal principal) {
        Post post = postService.editPost(postId, principal.getName(), newContent);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, Principal principal) {
        postService.deletePost(postId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<Post>> getPostsByUser(Principal principal) {
        List<Post> posts = postService.getPostsByUser(principal.getName());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<List<User>> getUsersWhoLikedPost(@PathVariable Long postId) {
        List<User> users = likeService.getUsersWhoLikedPost(postId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/liked")
    public ResponseEntity<List<Post>> getLikedPosts(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Post> posts = postService.getLikedPosts(user.getId());
        return ResponseEntity.ok(posts);
    }
}
