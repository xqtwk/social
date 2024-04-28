package main.proj.social.feed.follow;


import lombok.RequiredArgsConstructor;
import main.proj.social.user.UserRepository;
import main.proj.social.user.dto.UserPublicDto;
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

    public List<UserPublicDto> getFollowed(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return followRepository.findAllByFollowed(user).stream()
                .map(follow -> {
                    User followedUser = follow.getFollowed();
                    return new UserPublicDto(
                            followedUser.getId(),
                            followedUser.getUsername(),
                            followedUser.getLikes(),
                            followedUser.getPosts(),
                            followedUser.getFollows(),
                            followedUser.getFollowers()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<UserPublicDto> getFollowers(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return followRepository.findAllByFollower(user).stream()
                .map(follow -> {
                    User followedUser = follow.getFollower();
                    return new UserPublicDto(
                            followedUser.getId(),
                            followedUser.getUsername(),
                            followedUser.getLikes(),
                            followedUser.getPosts(),
                            followedUser.getFollows(),
                            followedUser.getFollowers()
                    );
                })
                .collect(Collectors.toList());
    }
}
