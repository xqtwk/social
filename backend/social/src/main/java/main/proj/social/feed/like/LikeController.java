package main.proj.social.feed.like;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import main.proj.social.feed.post.Post;
import main.proj.social.feed.post.PostRepository;
import main.proj.social.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/likes")
@Tag(name = "Likes")
public class LikeController {
    private final LikeService likeService;
    @Operation(summary = "Toggle like")
    @PostMapping("/{postId}")
    public ResponseEntity<String> toggleLike(@PathVariable Long postId, Principal principal) {
        String response = likeService.likeOrDislikePost(postId, principal.getName());
        return ResponseEntity.ok(response);
    }

}
