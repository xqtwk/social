package main.proj.social.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.proj.social.feed.follow.Follow;
import main.proj.social.feed.like.Like;
import main.proj.social.feed.post.Post;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPublicDto {
    private Long id;
    private String username;
    private List<Like> likes;
    private List<Post> posts;
    private List<Follow> follows;
    private List<Follow> followers;

}
