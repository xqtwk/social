package main.proj.social.feed.like;

import main.proj.social.feed.post.Post;
import main.proj.social.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByPost(Post post);

    // For finding likes by a specific user and post
    Optional<Like> findByUserAndPost(User user, Post post);

    List<Like> findByUser(User user);

    @Query("SELECT l.post FROM Like l WHERE l.user.id = :userId ORDER BY l.created_timestamp DESC")
    List<Post> findLikedPostsByUserId(@Param("userId") Long userId);

}
