package com.example.reddit.entities;

import com.example.reddit.exceptions.SpringRedditException;

import java.util.Arrays;

public enum VoteType {
    UPVOTE(1), DOWNVOTE(-1);

    private int direction;

    VoteType(int direction){
        this.direction = direction;
    }

    public Integer getDirection(){
        return direction;
    }

    public static VoteType lookup(int direction){
        return Arrays.stream(VoteType.values()).filter(value -> value.getDirection().equals(direction))
                .findFirst().orElseThrow(() -> new SpringRedditException("Vote not Found"));
    }
}
