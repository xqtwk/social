package main.proj.social.feed.follow;

import main.proj.social.user.UserRepository;
import main.proj.social.user.dto.UserPublicDto;
import main.proj.social.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {
    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FollowService followService;

    private User user1;
    private User user2;

    private User user3;
    @BeforeEach
    void setUp() {
        user1 = new User();  // Assume User has an all-args constructor or setters
        user1.setId(1L);
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        user3 = new User();
        user3.setId(3L);
        user3.setUsername("user3");
    }

    @Test
    void followUserSuccess() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findByUsername("user2")).thenReturn(Optional.of(user2));
        when(followRepository.findByFollowerAndFollowed(user1, user2)).thenReturn(Optional.empty());

        String result = followService.followOrUnfollowUser("user1", "user2");

        assertEquals("Followed", result);
        verify(followRepository).save(any(Follow.class));
    }

    @Test
    void unfollowUserSuccess() {
        Follow follow = new Follow(1L, user1, user2);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findByUsername("user2")).thenReturn(Optional.of(user2));
        when(followRepository.findByFollowerAndFollowed(user1, user2)).thenReturn(Optional.of(follow));

        String result = followService.followOrUnfollowUser("user1", "user2");

        assertEquals("Unfollowed", result);
        verify(followRepository).delete(follow);
    }
    void getFollowedUsersSuccess() {
        // Creating a follow relationship
        Follow follow = new Follow(1L, user1, user2);

        // Setting up the mock to return user1 when his username is searched
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));

        // Mocking findAllByFollowed to return a list with the follow instance when user1 is passed
        when(followRepository.findAllByFollowed(user1)).thenReturn(List.of(follow));

        // Executing the method to test
        List<UserPublicDto> followedUsers = followService.getFollowed("user1");

        // Assertions to check if the results are as expected
        assertNotNull(followedUsers);
        assertEquals(1, followedUsers.size());
        assertEquals(user2.getId(), followedUsers.getFirst().getId());
        assertEquals(user2.getUsername(), followedUsers.getFirst().getUsername());
    }
    @Test
    void followUserNotFound() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            followService.followOrUnfollowUser("user1", "user2");
        });
    }

    @Test
    void followedUserNotFound() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findByUsername("user2")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            followService.followOrUnfollowUser("user1", "user2");
        });
    }

    @Test
    void getFollowedUsersEmptyListSuccess() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(followRepository.findAllByFollowed(user1)).thenReturn(Collections.emptyList());

        List<UserPublicDto> followedUsers = followService.getFollowed("user1");

        assertTrue(followedUsers.isEmpty());
    }

    @Test
    void getFollowersEmptyListSuccess() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(followRepository.findAllByFollower(user1)).thenReturn(Collections.emptyList());

        List<UserPublicDto> followers = followService.getFollowers("user1");

        assertTrue(followers.isEmpty());
    }

    @Test
    void getFollowedUserNotFound() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            followService.getFollowed("user1");
        });
    }

    @Test
    void getFollowersUserNotFound() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            followService.getFollowers("user1");
        });
    }

    @Test
    void getFollowersCorrectDataReturned() {
        Follow follow = new Follow(1L, user3, user1);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(followRepository.findAllByFollower(user1)).thenReturn(List.of(follow));

        List<UserPublicDto> followers = followService.getFollowers("user1");

        assertNotNull(followers);
        assertEquals(1, followers.size());
        assertEquals(user3.getId(), followers.getFirst().getId());
        assertEquals(user3.getUsername(), followers.getFirst().getUsername());
    }
}