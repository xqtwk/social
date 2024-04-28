package main.proj.social.user;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import main.proj.social.user.dto.ChangePasswordRequest;
import main.proj.social.user.dto.UserPrivateDataResponse;
import main.proj.social.user.dto.UserPublicDataResponse;
import main.proj.social.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Tag(name = "Users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    @Operation( summary = "Change password")
    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get public user data")
    @GetMapping("/{username}")
    public ResponseEntity<UserPublicDataResponse> getPublicUserData(@PathVariable String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = optionalUser.get();
        UserPublicDataResponse userPublicDataResponse = new UserPublicDataResponse(
                user.getId(),
                user.getUsername());
        return ResponseEntity.ok(userPublicDataResponse);
    }

    @Operation(summary = "Get private user data")
    @GetMapping("/get-private-data")
    public ResponseEntity<UserPrivateDataResponse> getPrivateUserData(Principal connectedUser) {
        UserPrivateDataResponse userPrivateDataResponse = userService.getPrivateUserData(connectedUser);
        return ResponseEntity.ok(userPrivateDataResponse);
    }

}
