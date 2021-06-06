package com.example.reddit.controllers;

import com.example.reddit.entities.Subreddit;
import com.example.reddit.mappers.SubredditDto;
import com.example.reddit.services.SubredditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subreddit")
public class SubredditController {
    private final SubredditService subredditService;

    public SubredditController(SubredditService subredditService) {
        this.subredditService = subredditService;
    }

    @PostMapping
    public ResponseEntity<SubredditDto> createSubreddit(@RequestBody SubredditDto subredditRequest){
        SubredditDto subredditDto = subredditService.save(subredditRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subredditDto);
    }

    @GetMapping
    public ResponseEntity<List<SubredditDto>> getAll(){
        List<SubredditDto> subredditsDto = subredditService.getAll();
        return ResponseEntity.status(HttpStatus.OK)
                .body(subredditsDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubredditDto> getSubredditById(@PathVariable("id") Long id){
        SubredditDto subredditDto = subredditService.getSubredditById(id);
        return ResponseEntity.status(HttpStatus.OK).body(subredditDto);
    }
}
