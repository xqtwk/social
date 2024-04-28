package main.proj.social.security;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import main.proj.social.security.dto.*;
import main.proj.social.security.service.AuthenticationService;
import main.proj.social.security.tfa.MfaSetupResponse;
import main.proj.social.security.tfa.MfaToggleRequest;
import main.proj.social.security.tfa.TwoFactorAuthenticationService;
import main.proj.social.user.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.NoSuchElementException;

import static main.proj.social.security.service.ErrorService.extractDataIntegrityViolationExceptionErrorMessage;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final TwoFactorAuthenticationService twoFactorAuthenticationService;

    @Operation(summary = "New user registration")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) {
        try {
            AuthenticationResponse response = authenticationService.register(request);
            if (!request.isMfaEnabled()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.ok(AuthenticationResponse.builder()
                        .mfaEnabled(true)
                        .secretImageUri(response.getSecretImageUri())
                        .build());
            }
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(extractDataIntegrityViolationExceptionErrorMessage(e));
        }
    }
    @Operation(summary = "Authentication")
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse response = authenticationService.authenticate(request);

            if (response.isMfaEnabled()) {
                // MFA Enabled - Provide partial response for MFA flow
                return ResponseEntity.ok(AuthenticationResponse.builder()
                        .mfaEnabled(true)
                        .build());
            } else {
                // Successful Authentication - Return full response
                return ResponseEntity.ok(response);
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid username or password"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid username or password"));
        }
    }

    @Operation(summary = "Auth token refresh")
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @Operation(summary = "Mfa code verification")
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(
            @RequestBody VerificationRequest verificationRequest
    ) {
        return ResponseEntity.ok(authenticationService.verifyCode(verificationRequest));
    }

    @Operation(summary = "Toggle on/off of mfa")
    @PostMapping("/toggle-mfa")
    public ResponseEntity<?> toggleMfa(@RequestBody MfaToggleRequest request, Principal principal) {
        System.out.println(request.getOtpCode());
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (request.isEnableMfa()) {
            // If enabling MFA
            String newSecret = request.getSecret();
            if (twoFactorAuthenticationService.isOtpValid(newSecret, request.getOtpCode())) {
                user.setMfaEnabled(true);
                user.setSecret(newSecret);

                userRepository.save(user);
                System.out.println(user.getSecret());
                System.out.println(user.isMfaEnabled());
                System.out.println("DONE");
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            // If disabling MFA, verify the OTP code first
            if (twoFactorAuthenticationService.isOtpValid(user.getSecret(), request.getOtpCode())) {
                user.setMfaEnabled(false);
                user.setSecret(null);
                userRepository.save(user);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
    }

    @Operation(summary = "Generate QR-code for mfa")
    @GetMapping("/mfa-setup")
    public ResponseEntity<?> getMfaSetup(Principal principal) {
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isMfaEnabled()) {
            String newSecret = twoFactorAuthenticationService.generateNewSecret();
            // Do not save the secret yet; save it after user confirms enabling MFA
            String qrCodeImageUri = twoFactorAuthenticationService.generateQrCodeImageUri(newSecret);
            return ResponseEntity.ok(new MfaSetupResponse(qrCodeImageUri, newSecret)); // Include the newSecret in the response
        } else {
            return ResponseEntity.ok(new MfaSetupResponse(null, null));
        }
    }

}
