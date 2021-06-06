package com.example.reddit.mappers;

import com.example.reddit.entities.VoteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteRequest {
    private Long postId;
    private VoteType voteType;
}
