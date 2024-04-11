package main.proj.social.feed.post;

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

    private Date created_timestamp;

    @PrePersist
    protected void onCreate() {
        created_timestamp = new Date(); // Set timestamp when the entity is persisted
    }
}