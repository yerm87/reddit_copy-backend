package com.example.reddit.repos;

import com.example.reddit.entities.Comment;
import com.example.reddit.entities.Post;
import com.example.reddit.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}
