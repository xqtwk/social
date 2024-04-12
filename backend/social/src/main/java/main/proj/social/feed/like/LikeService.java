package main.proj.social.feed.like;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import main.proj.social.feed.post.Post;
import main.proj.social.feed.post.PostRepository;
import main.proj.social.user.UserRepository;
import main.proj.social.user.entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    public String likeOrDislikePost(Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        return likeRepository.findByUserAndPost(user, post)
                .map(like -> {
                    likeRepository.delete(like);
                    return "Disliked";
                })
                .orElseGet(() -> {
                    Like like = Like.builder()
                            .user(user)
                            .post(post)
                            .build();
                    likeRepository.save(like);
                    return "Liked";
                });
    }
    public List<Post> getLikedPosts(User user) {
        return likeRepository.findByUser(user)
                .stream()
                .map(Like::getPost)
                .collect(Collectors.toList());
    }

    public List<User> getUsersWhoLikedPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        return likeRepository.findByPost(post).stream()
                .map(Like::getUser)
                .collect(Collectors.toList());
    }

}
