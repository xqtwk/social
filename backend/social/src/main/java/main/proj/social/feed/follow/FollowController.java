package main.proj.social.feed.follow;

import lombok.RequiredArgsConstructor;
import main.proj.social.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{followedUsername}")
    public ResponseEntity<String> toggleFollow(@PathVariable String followedUsername, Principal principal) {
        String response = followService.followOrUnfollowUser(principal.getName(), followedUsername);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/following")
    public ResponseEntity<List<User>> getFollowedUsers(Principal principal) {
        List<User> users = followService.getFollowedUsers(principal.getName());
        return ResponseEntity.ok(users);
    }
}
