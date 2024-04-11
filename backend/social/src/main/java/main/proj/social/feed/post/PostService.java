package main.proj.social.feed.post;

import jakarta.persistence.EntityNotFoundException;


import lombok.RequiredArgsConstructor;
import main.proj.social.feed.like.LikeRepository;
import main.proj.social.user.UserRepository;
import main.proj.social.user.entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public List<Post> getNewsFeedForUser(Long userId) {
        return postRepository.findPostsForUserByFollow(userId);
    }

    @Transactional
    public Post createPost(String username, PostRequest postRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Post post = new Post();
        post.setAuthor(user);
        post.setContent(postRequest.getContent());

        if (postRequest.getParentId() != null) {
            Post parent = postRepository.findById(postRequest.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent post not found"));
            post.setParent(parent);
        }

        return postRepository.save(post);
    }

    @Transactional
    public Post editPost(Long postId, String username, String newContent) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new IllegalStateException("Cannot edit someone else's post");
        }
        post.setContent(newContent);
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new IllegalStateException("Cannot delete someone else's post");
        }
        postRepository.delete(post);
    }

    public List<Post> getPostsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return postRepository.findByAuthor(user);
    }

    public List<Post> getLikedPosts(Long userId) {
        return likeRepository.findLikedPostsByUserId(userId);
    }

    public List<Post> getAnswersToPost(Long parentPostId) {
        return postRepository.findByParentId(parentPostId);
    }
}
