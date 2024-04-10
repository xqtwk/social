package main.proj.social.user;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import main.proj.social.user.dto.ChangePasswordRequest;
import main.proj.social.user.dto.UserPrivateDataResponse;
import main.proj.social.user.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        if (!request.newPassword().equals(request.confirmationPassword())) {
            throw new IllegalStateException("Password confirmation failed");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);
    }
    public String getUsernameById(Long id) {
        return userRepository.findById(id)
                .map(User::getUsername)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Transactional
    public UserPrivateDataResponse getPrivateUserData(Principal connectedUser) {
        User principalUser = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        User user = userRepository.findByUsername(principalUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new UserPrivateDataResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isMfaEnabled()
        );
    }

}
