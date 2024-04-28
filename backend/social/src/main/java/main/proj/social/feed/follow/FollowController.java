package main.proj.social.feed.follow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import main.proj.social.user.dto.UserPublicDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/v1/follows")
@RequiredArgsConstructor
@Tag(name = "Follows")
public class FollowController {
    private final FollowService followService;

    @Operation(summary = "Toggle follow")
    @PostMapping("/{followedUsername}")
    public ResponseEntity<String> toggleFollow(@PathVariable String followedUsername, Principal principal) {
        String response = followService.followOrUnfollowUser(principal.getName(), followedUsername);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get users that user follows")
    @GetMapping("/user/following/{username}")
    public ResponseEntity<List<UserPublicDto>> getFollowedUsers(@PathVariable String username) {
        List<UserPublicDto> users = followService.getFollowed(username);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user followers")
    @GetMapping("/user/follows/{username}")
    public ResponseEntity<List<UserPublicDto>> getUserFollowers(@PathVariable String username) {
        List<UserPublicDto> users = followService.getFollowers(username);
        return ResponseEntity.ok(users);
    }
}
