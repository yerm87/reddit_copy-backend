package com.example.reddit.controllers;

import com.example.reddit.mappers.PostRequest;
import com.example.reddit.mappers.PostResponse;
import com.example.reddit.services.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest postRequest){
        PostResponse postResponse = postService.createPost(postRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostResponse>> getAllPosts(){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id){
        PostResponse postResponse = postService.getPostById(id);
        return ResponseEntity.status(HttpStatus.OK).body(postResponse);
    }

    @GetMapping("/by-subreddit/{id}")
    public ResponseEntity<List<PostResponse>> getPostsBySubredditId(@PathVariable Long id){
        List<PostResponse> postResponses = postService.getPostsBySubredditId(id);
        return ResponseEntity.status(HttpStatus.OK).body(postResponses);
    }

    @GetMapping("/by-user/{username}")
    public ResponseEntity<List<PostResponse>> getPostsByUsername(@PathVariable String username){
        List<PostResponse> postResponses = postService.getPostsByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(postResponses);
    }
}
