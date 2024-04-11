package main.proj.social.feed.follow;

import main.proj.social.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollower(User follower);
    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);
}
