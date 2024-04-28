package main.proj.social.feed.post;

import jakarta.persistence.EntityNotFoundException;


import lombok.RequiredArgsConstructor;
import main.proj.social.feed.like.LikeRepository;
import main.proj.social.fileManagement.FileService;
import main.proj.social.fileManagement.exceptions.StorageException;
import main.proj.social.user.UserRepository;
import main.proj.social.user.entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final FileService fileService;
    public List<Post> getNewsFeedForUser(Long userId) {
        return postRepository.findPostsForUserByFollow(userId);
    }

    @Transactional
    public Post createPost(String username, PostRequest postRequest, List<MultipartFile> photos) throws StorageException {
        System.out.println("entering createpost");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        System.out.println("after user check");

        Post post = Post.builder()
                .author(user)
                .content(postRequest.getContent())
                .photoPaths(new ArrayList<>())
                .build();

        if (!photos.isEmpty()) {
            List<String> photoUrls = uploadPhotos(username, photos);
            post.setPhotoPaths(photoUrls);
        }


        if (postRequest.getParentId() != null) {
            Post parent = postRepository.findById(postRequest.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent post not found"));
            post.setParent(parent);
        }

        return postRepository.save(post);
    }

    private List<String> uploadPhotos(String username, List<MultipartFile> photos) throws StorageException {
        List<String> photoUrls = new ArrayList<>();
        for (MultipartFile photo : photos) {
            if (!photo.isEmpty()) {
                String photoUrl = fileService.storePostPhoto(photo, username);
                photoUrls.add(photoUrl);
            } else {
                throw new StorageException("Failed to store empty file.");
            }
        }
        return photoUrls;
    }

    public Post editPost(Long postId, String username, String newContent, List<MultipartFile> newPhotos) throws StorageException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new IllegalStateException("Cannot edit someone else's post");
        }

        post.setContent(newContent);

        // Optionally clear old photos or append new ones
        List<String> updatedPhotoUrls = uploadPhotos(username, newPhotos);
        post.getPhotoPaths().addAll(updatedPhotoUrls); // Or set new list if replacing

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
