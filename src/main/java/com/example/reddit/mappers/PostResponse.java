package com.example.reddit.mappers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private Long id;
    private String postName;
    private String url;
    private String description;
    private String subredditName;
    private String username;
    private Integer commentCount;
    private Integer voteCount;
    private boolean upVote;
    private boolean downVote;
}
