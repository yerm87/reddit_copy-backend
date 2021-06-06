package com.example.reddit.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Post name cannot be empty or null")
    private String postName;
    @Nullable
    private String url;
    @Nullable
    @Lob
    private String description;
    private Integer voteCount = 0;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private Instant createdDate;
    @ManyToOne(fetch = FetchType.LAZY)
    private Subreddit subreddit;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id")
    private List<Comment> comments;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id")
    private List<Vote> votes;

    public String toString(){
        return "post with id: " + id + " and name: \\n" +
                postName;
    }
}
