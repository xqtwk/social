package main.proj.social.feed.post;

import jakarta.persistence.EntityNotFoundException;
import main.proj.social.feed.like.LikeRepository;
import main.proj.social.user.UserRepository;
import main.proj.social.user.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private PostService postService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        Mockito.reset(postRepository, userRepository);  // Reset mocks to clear any previous interactions or returns

        user = new User(); // Assuming User has an all-args constructor or setters
        user.setId(1L);
        user.setUsername("user1");

        post = new Post();
        post.setId(1L);
        post.setAuthor(user);
        post.setContent("Initial content");
    }
    @AfterEach
    void tearDown() {
        Mockito.reset(postRepository, userRepository);  // Ensure mocks are clean for the next test
    }
    @Test
    void getNewsFeedForUser() {
        List<Post> expectedPosts = Arrays.asList(post);
        when(postRepository.findPostsForUserByFollow(user.getId())).thenReturn(expectedPosts);

        List<Post> posts = postService.getNewsFeedForUser(user.getId());

        assertNotNull(posts);
        assertFalse(posts.isEmpty());
        assertEquals(1, posts.size());
        verify(postRepository).findPostsForUserByFollow(user.getId());
    }
    @Test
    void createPostSuccess() {
        // Set up specific for this test
        PostRequest postRequest= new PostRequest("Test content", null); // For creating a new post, parentId is null
        User newUser = new User(); // Assuming User has an all-args constructor or setters
        newUser.setId(1L);
        newUser.setUsername("user1");

        Post newPost = new Post();
        newPost.setId(1L);
        newPost.setAuthor(user);
        newPost.setContent("Initial content");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(newUser));

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post savedPost = invocation.getArgument(0);
            savedPost.setId(1L);  // Simulate setting ID upon saving as DB would do
            return savedPost;
        });

        // Execute
        Post newNewPost = postService.createPost("user1", postRequest);

        // Validate
        assertNotNull(newNewPost);
        assertEquals("Test content", newNewPost.getContent());
        assertNotNull(newNewPost.getId());
        verify(postRepository).save(any(Post.class));
    }


    @Test
    void createPostUserNotFound() {
        PostRequest postRequest= new PostRequest("Test content", null); // For creating a new post, parentId is null

        when(userRepository.findByUsername("user1")).thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class, () -> postService.createPost("user1", postRequest));
    }

    @Test
    void editPostSuccess() {
        when(postRepository.findById(1L)).thenReturn(java.util.Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post updatedPost = postService.editPost(1L, "user1", "Updated content");

        assertNotNull(updatedPost);
        assertEquals("Updated content", updatedPost.getContent());
        verify(postRepository).save(post);
    }

    @Test
    void editPostNotFound() {
        when(postRepository.findById(1L)).thenThrow(new EntityNotFoundException("Post not found"));
        assertThrows(EntityNotFoundException.class, () -> postService.editPost(1L, "user1", "Updated content"));
    }
    @Test
    void editPostUnauthorizedUser() {
        when(postRepository.findById(1L)).thenReturn(java.util.Optional.of(post));
        assertThrows(IllegalStateException.class, () -> postService.editPost(1L, "user2", "Updated content"));
    }

    @Test
    void deletePostSuccess() {
        when(postRepository.findById(1L)).thenReturn(java.util.Optional.of(post));
        doNothing().when(postRepository).delete(post);

        postService.deletePost(1L, "user1");

        verify(postRepository).delete(post);
    }

    @Test
    void deletePostNotFound() {
        when(postRepository.findById(1L)).thenThrow(new EntityNotFoundException("Post not found"));

        assertThrows(EntityNotFoundException.class, () -> postService.deletePost(1L, "user1"));
    }

    @Test
    void deletePostUnauthorizedUser() {
        when(postRepository.findById(1L)).thenReturn(java.util.Optional.of(post));

        assertThrows(IllegalStateException.class, () -> postService.deletePost(1L, "user2"));
    }

    @Test
    void getPostsByUserSuccess() {
        List<Post> expectedPosts = Arrays.asList(post);
        when(userRepository.findByUsername("user1")).thenReturn(java.util.Optional.of(user));
        when(postRepository.findByAuthor(user)).thenReturn(expectedPosts);

        List<Post> posts = postService.getPostsByUser("user1");

        assertNotNull(posts);
        assertFalse(posts.isEmpty());
        assertEquals(1, posts.size());
        verify(postRepository).findByAuthor(user);
    }
    @Test
    void getPostsByUserNotFound() {
        when(userRepository.findByUsername("user1")).thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class, () -> postService.getPostsByUser("user1"));
    }

    @Test
    void getLikedPosts() {
        List<Post> likedPosts = Arrays.asList(post);
        when(likeRepository.findLikedPostsByUserId(user.getId())).thenReturn(likedPosts);

        List<Post> result = postService.getLikedPosts(user.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(likeRepository).findLikedPostsByUserId(user.getId());
    }
    /*@Autowired
    private PostService postService;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    void deletePostSuccess() {
        // Assuming both User and Post classes use Lombok @Builder
        User user = User.builder()
                .id(1L)
                .username("user1")
                .email("email@example.com")
                .password("password")
                .mfaEnabled(true)
                .build();

        Post post = Post.builder()
                .id(1L)
                .author(user)
                .content("Content")
                .likes(new ArrayList<>()) // Assuming likes is a list
                .created_timestamp(new Date())
                .build();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        doNothing().when(postRepository).delete(post);

        postService.deletePost(1L, "user1");

        verify(postRepository).delete(post);
    }
    @Test
    void deletePostFail() {
        // Assuming both User and Post classes use Lombok @Builder
        User user = User.builder()
                .id(1L)
                .username("user1")
                .email("email@example.com")
                .password("password")
                .mfaEnabled(true)
                .build();

        Post post = Post.builder()
                .id(1L)
                .author(user)
                .content("Content")
                .likes(new ArrayList<>()) // Assuming likes is a list
                .created_timestamp(new Date())
                .build();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        doThrow(new RuntimeException("Deletion failed")).when(postRepository).delete(post);

        assertThrows(RuntimeException.class, () -> postService.deletePost(1L, "user1"));

        verify(postRepository).delete(post);
    }*/
}