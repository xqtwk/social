package main.proj.social.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.proj.social.feed.follow.Follow;
import main.proj.social.feed.like.Like;
import main.proj.social.feed.post.Post;
import main.proj.social.user.entity.enums.Role;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPrivateDto {
    private long id;
    private String username;
    private String email;
    private Role role;
    private boolean mfaEnabled;
    private List<Like> likes;
    private List<Post> posts;
    private List<Follow> follows;
    private List<Follow> followers;
}
