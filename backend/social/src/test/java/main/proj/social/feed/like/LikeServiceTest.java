package main.proj.social.feed.like;

import main.proj.social.feed.post.Post;
import main.proj.social.feed.post.PostRepository;
import main.proj.social.user.UserRepository;
import main.proj.social.user.dto.UserPublicDto;
import main.proj.social.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private LikeService likeService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("user1");

        post = new Post();
        post.setId(1L);
        post.setAuthor(user);
        post.setContent("Post content");
    }
    @Test
    void getLikedPostsSuccess() {
        Like like1 = new Like(1L, user, post, new Date());
        Like like2 = new Like(2L, user, post, new Date());

        when(likeRepository.findByUser(user)).thenReturn(Arrays.asList(like1, like2));

        List<Post> likedPosts = likeService.getLikedPosts(user);

        assertNotNull(likedPosts);
        assertEquals(2, likedPosts.size());
        assertTrue(likedPosts.contains(post));

        verify(likeRepository).findByUser(user);
    }

    @Test
    void getUsersWhoLikedPostSuccess() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        Post post = new Post(); // Assuming Post has at least an ID setter or constructor
        post.setId(1L);

        Like like1 = new Like(1L, user1, post, new Date());
        Like like2 = new Like(2L, user2, post, new Date());

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.findByPost(post)).thenReturn(Arrays.asList(like1, like2));

        List<UserPublicDto> usersWhoLiked = likeService.getUsersWhoLikedPost(1L);

        assertNotNull(usersWhoLiked);
        assertEquals(2, usersWhoLiked.size());
        assertEquals(usersWhoLiked.get(0).getId(), user1.getId());
        assertEquals(usersWhoLiked.get(1).getId(), user2.getId());
        assertEquals(usersWhoLiked.get(0).getUsername(), user1.getUsername());
        assertEquals(usersWhoLiked.get(1).getUsername(), user2.getUsername());

        verify(likeRepository).findByPost(post);
    }

    @Test
    void likeOrDislikePostLikeSuccess() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());

        String result = likeService.likeOrDislikePost(1L, "user1");

        assertEquals("Liked", result);
        verify(likeRepository).save(any(Like.class));
    }
}