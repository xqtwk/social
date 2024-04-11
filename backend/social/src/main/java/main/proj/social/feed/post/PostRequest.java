package main.proj.social.feed.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostRequest {
    private String content;
    private Long parentId;
}
