package main.proj.social.feed.like;

import jakarta.persistence.*;
import lombok.*;
import main.proj.social.feed.post.Post;
import main.proj.social.user.entity.User;
import org.springframework.lang.Nullable;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_like")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "liker_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private Date created_timestamp;

    @PrePersist
    protected void onCreate() {
        created_timestamp = new Date(); // Set timestamp when the entity is persisted
    }
}
