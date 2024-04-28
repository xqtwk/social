package main.proj.social.feed.post;

import jakarta.persistence.EntityNotFoundException;
import main.proj.social.feed.like.LikeRepository;
import main.proj.social.fileManagement.FileService;
import main.proj.social.fileManagement.exceptions.StorageException;
import main.proj.social.user.UserRepository;
import main.proj.social.user.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    @Mock
    private FileService fileService;

    @InjectMocks
    private PostService postService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        Mockito.reset(postRepository, userRepository);

        user = new User();
        user.setId(1L);
        user.setUsername("user1");

        post = new Post();
        post.setId(1L);
        post.setAuthor(user);
        post.setContent("Initial content");
    }
    @AfterEach
    void tearDown() {
        Mockito.reset(postRepository, userRepository);
    }

   @Test
   void createPostShouldThrowStorageException() throws StorageException {
       MultipartFile emptyPhoto = new MockMultipartFile("empty.jpg", "empty.jpg", "image/jpeg", new byte[0]);
       List<MultipartFile> photos = Collections.singletonList(emptyPhoto);
       PostRequest postRequest = new PostRequest("Content", null);

       when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

       assertThrows(StorageException.class, () -> postService.createPost("user1", postRequest, photos),
               "Expected StorageException was not thrown");
   }
    @Test
    void storePostPhoto_ShouldThrow_WhenFileIsEmpty() throws StorageException {
        MultipartFile emptyFile = new MockMultipartFile("empty.jpg", "empty.jpg", "image/jpeg", new byte[0]);
        when(fileService.storePostPhoto(emptyFile, "user1")).thenThrow(new StorageException("Failed to store file."));
        assertThrows(StorageException.class, () -> fileService.storePostPhoto(emptyFile, "user1"),
                "FileService should throw StorageException for empty files");
    }
    @Test
    void testEmptyMultipartFile() {
        MultipartFile emptyFile = new MockMultipartFile("empty.jpg", "empty.jpg", "image/jpeg", new byte[0]);
        assertTrue(emptyFile.isEmpty(), "The file should be recognized as empty.");
    }
    @Test
    void testMockMultipartFile() {
        MultipartFile emptyFile = new MockMultipartFile("empty.jpg", "empty.jpg", "image/jpeg", new byte[0]);
        assertTrue(emptyFile.isEmpty(), "The file should be empty but it's not.");
        assertEquals(0, emptyFile.getSize(), "File size should be 0.");
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
    void createPostWithPhotosSuccess() throws StorageException {
        List<MultipartFile> photos = new ArrayList<>();
        photos.add(new MockMultipartFile("photo1.jpg", "photo1.jpg", "image/jpeg", new byte[] {1, 2, 3}));
        List<String> photoUrls = Collections.singletonList("photo1.jpg");

        PostRequest postRequest = new PostRequest("Test content", null);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(fileService.storePostPhoto(any(MultipartFile.class), eq("user1"))).thenReturn("photo1.jpg");
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post createdPost = postService.createPost("user1", postRequest, photos);

        assertNotNull(createdPost);
        assertEquals("Test content", createdPost.getContent());
        assertTrue(createdPost.getPhotoPaths().contains("photo1.jpg"));
        verify(fileService, times(1)).storePostPhoto(any(MultipartFile.class), eq("user1"));
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void editPostWithPhotosSuccess() throws StorageException {
        List<MultipartFile> newPhotos = new ArrayList<>();
        newPhotos.add(new MockMultipartFile("newPhoto.jpg", "newPhoto.jpg", "image/jpeg", new byte[] {4, 5, 6}));
        List<String> newPhotoUrls = Collections.singletonList("newPhoto.jpg");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(fileService.storePostPhoto(any(MultipartFile.class), eq("user1"))).thenReturn("newPhoto.jpg");
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post updatedPost = postService.editPost(1L, "user1", "Updated content", newPhotos);

        assertNotNull(updatedPost);
        assertEquals("Updated content", updatedPost.getContent());
        assertTrue(updatedPost.getPhotoPaths().contains("newPhoto.jpg"));
        verify(fileService, times(1)).storePostPhoto(any(MultipartFile.class), eq("user1"));
        verify(postRepository).save(post);
    }
    @Test
    void createPostWithoutPhotosSuccess() throws StorageException {
        // Given
        PostRequest postRequest = new PostRequest("Test content", null); // For creating a new post, parentId is null
        List<MultipartFile> emptyPhotos = new ArrayList<>(); // No photos are included

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post savedPost = invocation.getArgument(0);
            savedPost.setId(1L);  // Simulate setting ID upon saving as DB would do
            return savedPost;
        });

        // When
        Post createdPost = postService.createPost("user1", postRequest, emptyPhotos);

        // Then
        assertNotNull(createdPost);
        assertEquals("Test content", createdPost.getContent());
        assertTrue(createdPost.getPhotoPaths().isEmpty()); // Ensure no photo paths are stored
        verify(postRepository).save(any(Post.class));
        verify(fileService, never()).storePostPhoto(any(MultipartFile.class), anyString()); // Ensure no photo upload attempt
    }


    @Test
    void createPostUserNotFound() {
        PostRequest postRequest = new PostRequest("Test content", null);
        List<MultipartFile> emptyPhotos = new ArrayList<>(); // Pass an empty list of photos

        when(userRepository.findByUsername("user1")).thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class, () -> postService.createPost("user1", postRequest, emptyPhotos));
    }

    @Test
    void editPostSuccess() throws StorageException {
        List<MultipartFile> emptyPhotos = new ArrayList<>();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post updatedPost = postService.editPost(1L, "user1", "Updated content", emptyPhotos);

        assertNotNull(updatedPost);
        assertEquals("Updated content", updatedPost.getContent());
        assertTrue(updatedPost.getPhotoPaths().isEmpty()); // Check that no new photos were added
        verify(postRepository).save(post);
        verify(fileService, never()).storePostPhoto(any(MultipartFile.class), anyString()); // Ensure no photo was processed
    }

    @Test
    void editPostNotFound() {
        List<MultipartFile> emptyPhotos = new ArrayList<>();
        when(postRepository.findById(1L)).thenThrow(new EntityNotFoundException("Post not found"));

        assertThrows(EntityNotFoundException.class, () -> postService.editPost(1L, "user1", "Updated content", emptyPhotos));
    }
    @Test
    void editPostUnauthorizedUser() {
        List<MultipartFile> emptyPhotos = new ArrayList<>();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(IllegalStateException.class, () -> postService.editPost(1L, "user2", "Updated content", emptyPhotos));
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
    @Test
    void verifyParameterPassedToStorePostPhoto() throws StorageException {
        MultipartFile photo = new MockMultipartFile("photo.jpg", "photo.jpg", "image/jpeg", new byte[] {1, 2, 3});
        List<MultipartFile> photos = Collections.singletonList(photo);
        PostRequest postRequest = new PostRequest("Test content", null);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        postService.createPost("user1", postRequest, photos);

        ArgumentCaptor<MultipartFile> fileCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        verify(fileService).storePostPhoto(fileCaptor.capture(), eq("user1"));
        assertEquals("photo.jpg", fileCaptor.getValue().getOriginalFilename());
    }

}