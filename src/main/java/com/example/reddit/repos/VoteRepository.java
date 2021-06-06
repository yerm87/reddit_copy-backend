package com.example.reddit.repos;

import com.example.reddit.entities.Post;
import com.example.reddit.entities.User;
import com.example.reddit.entities.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByIdDesc(Post post, User user);

}
