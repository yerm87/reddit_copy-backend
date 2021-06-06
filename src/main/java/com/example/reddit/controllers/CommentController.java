package com.example.reddit.controllers;

import com.example.reddit.mappers.CommentDto;
import com.example.reddit.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentDto commentDto){
        commentService.createComment(commentDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/by-post/{id}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable("id") Long postId){
        List<CommentDto> commentsDto = commentService.getAllCommentsByPostId(postId);
        return ResponseEntity.status(HttpStatus.OK).body(commentsDto);
    }

    @GetMapping("/by-user/{username}")
    public ResponseEntity<List<CommentDto>> getCommentsByUsername(@PathVariable("username") String username){
        List<CommentDto> commentsDto = commentService.getCommentsByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(commentsDto);
    }
}
