package main.proj.social.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.proj.social.user.entity.enums.Role;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPrivateDataResponse {
    private long id;
    private String username;
    private String email;
    private Role role;
    private boolean mfaEnabled;
}
