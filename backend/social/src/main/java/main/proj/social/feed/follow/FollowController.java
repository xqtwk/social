package main.proj.social.feed.follow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "Get users that user follows")
    @GetMapping("/user/following")
    public ResponseEntity<List<User>> getFollowedUsers(Principal principal) {
        List<User> users = followService.getFollowedUsers(principal.getName());
        return ResponseEntity.ok(users);
    }
}
