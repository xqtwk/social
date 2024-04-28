package main.proj.social.feed.follow;


import lombok.RequiredArgsConstructor;
import main.proj.social.user.UserRepository;
import main.proj.social.user.dto.UserPublicDataResponse;
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
                    Follow follow = Follow.builder().
                            follower(follower).
                            followed(followed)
                            .build();
                    followRepository.save(follow);
                    return "Followed";
                });
    }

    public List<UserPublicDataResponse> getFollowed(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return followRepository.findAllByFollowed(user).stream()
                .map(follow -> new UserPublicDataResponse(follow.getFollowed().getId(), follow.getFollowed().getUsername()))
                .collect(Collectors.toList());
    }

    public List<UserPublicDataResponse> getFollowers(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return followRepository.findAllByFollower(user).stream()
                .map(follow -> new UserPublicDataResponse(follow.getFollower().getId(), follow.getFollower().getUsername()))
                .collect(Collectors.toList());
    }
}
