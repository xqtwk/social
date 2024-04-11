package main.proj.social.feed.follow;


import lombok.RequiredArgsConstructor;
import main.proj.social.user.UserRepository;
import main.proj.social.user.entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    @Transactional
    public String followOrUnfollowUser(String username, String followedUsername) {
        User follower = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Follower not found"));
        User followed = userRepository.findByUsername(followedUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Followed user not found"));

        return followRepository.findByFollowerAndFollowed(follower, followed)
                .map(follow -> {
                    followRepository.delete(follow);
                    return "Unfollowed";
                })
                .orElseGet(() -> {
                    Follow follow = new Follow(null, follower, followed);
                    followRepository.save(follow);
                    return "Followed";
                });
    }

    public List<User> getFollowedUsers(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return followRepository.findByFollower(user).stream()
                .map(Follow::getFollowed)
                .collect(Collectors.toList());
    }
}
