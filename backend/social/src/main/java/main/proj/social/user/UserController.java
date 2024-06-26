package main.proj.social.user;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import main.proj.social.fileManagement.FileService;
import main.proj.social.fileManagement.exceptions.StorageException;
import main.proj.social.user.dto.ChangePasswordRequest;
import main.proj.social.user.dto.UserPrivateDto;
import main.proj.social.user.dto.UserPublicDto;
import main.proj.social.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Tag(name = "Users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final FileService fileService;
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
    public ResponseEntity<UserPublicDto> getPublicUserData(@PathVariable String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.map(user -> ResponseEntity.ok(userService.getPublicUserData(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @Operation(summary = "Get private user data")
    @GetMapping("/get-private-data")
    public ResponseEntity<UserPrivateDto> getPrivateUserData(Principal connectedUser) {
        UserPrivateDto userPrivateDto = userService.getPrivateUserData(connectedUser);
        return ResponseEntity.ok(userPrivateDto);
    }

    @PostMapping("/avatar")
    public ResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            fileService.storeAvatar(file, principal.getName());
            return ResponseEntity.ok("Avatar uploaded successfully");
        } catch (StorageException e) {
            return ResponseEntity.badRequest().body("Failed to upload avatar: " + e.getMessage());
        }
    }

}
