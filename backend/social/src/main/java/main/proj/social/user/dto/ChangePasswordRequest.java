package main.proj.social.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;


@Builder
public record ChangePasswordRequest(String currentPassword, String newPassword, String confirmationPassword) {
    @JsonCreator
    public ChangePasswordRequest(@JsonProperty("currentPassword") String currentPassword,
                                 @JsonProperty("newPassword") String newPassword,
                                 @JsonProperty("confirmationPassword") String confirmationPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmationPassword = confirmationPassword;
    }
}
