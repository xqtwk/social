package main.proj.social.feed;

import lombok.RequiredArgsConstructor;
import main.proj.social.feed.post.Post;
import main.proj.social.feed.post.PostRepository;
import main.proj.social.feed.post.PostService;
import main.proj.social.user.UserRepository;
import main.proj.social.user.entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NewsFeedController {
    private final PostService postService;
    private final UserRepository userRepository;

    @GetMapping
    public List<Post> getNewsFeed(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return postService.getNewsFeedForUser(user.getId());
    }
}
