package main.proj.social.feed.follow;

import main.proj.social.user.UserRepository;
import main.proj.social.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
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

    @BeforeEach
    void setUp() {
        user1 = new User();  // Assume User has an all-args constructor or setters
        user1.setId(1L);
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
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
    @Test
    void getFollowedUsersSuccess() {
        Follow follow = new Follow(1L, user1, user2);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(followRepository.findByFollower(user1)).thenReturn(Arrays.asList(follow));

        List<User> followedUsers = followService.getFollowedUsers("user1");

        assertNotNull(followedUsers);
        assertFalse(followedUsers.isEmpty());
        assertTrue(followedUsers.contains(user2));
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
}