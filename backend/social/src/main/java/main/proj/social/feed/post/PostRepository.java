package main.proj.social.feed.post;

import main.proj.social.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p " +
           "JOIN Follow f ON p.author.id = f.followed.id " +
           "WHERE f.follower.id = :userId " +
           "ORDER BY p.created_timestamp DESC")
    List<Post> findPostsForUserByFollow(@Param("userId") Long userId);

    List<Post> findByAuthor(User user);

    List<Post> findByParentId(Long parentId);

}
