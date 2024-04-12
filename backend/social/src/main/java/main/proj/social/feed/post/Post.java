package main.proj.social.feed.post;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import main.proj.social.feed.like.Like;
import main.proj.social.user.entity.User;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private String content;

    @OneToMany(mappedBy = "post")
    private List<Like> likes;

    @ManyToOne
    @Nullable
    @JoinColumn(name = "parent_id")
    private Post parent;  // Link to the parent post if this is a reply

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> replies;  // Replies to this post

    private Date created_timestamp;

    @PrePersist
    protected void onCreate() {
        created_timestamp = new Date(); // Set timestamp when the entity is persisted
    }
}