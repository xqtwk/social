package main.proj.social.feed.follow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import main.proj.social.user.dto.UserPublicDataResponse;
import main.proj.social.user.entity.User;
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

    @Operation(security = {}, summary = "Get users that user follows")
    @GetMapping("/user/following/{username}")
    public ResponseEntity<List<UserPublicDataResponse>> getFollowedUsers(@PathVariable String username) {
        List<UserPublicDataResponse> users = followService.getFollowed(username);
        return ResponseEntity.ok(users);
    }

    @Operation(security = {}, summary = "Get user followers")
    @GetMapping("/user/follows/{username}")
    public ResponseEntity<List<UserPublicDataResponse>> getUserFollowers(@PathVariable String username) {
        List<UserPublicDataResponse> users = followService.getFollowers(username);
        return ResponseEntity.ok(users);
    }
}
