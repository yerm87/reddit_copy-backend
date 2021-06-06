package com.example.reddit.services;

import com.example.reddit.entities.Post;
import com.example.reddit.entities.Subreddit;
import com.example.reddit.entities.User;
import com.example.reddit.exceptions.SpringRedditException;
import com.example.reddit.mappers.SubredditDto;
import com.example.reddit.repos.SubredditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubredditService {
    private final SubredditRepository subredditRepository;
    private final AuthService authService;

    @Autowired
    public SubredditService(SubredditRepository subredditRepository, AuthService authService) {
        this.subredditRepository = subredditRepository;
        this.authService = authService;
    }

    @Transactional
    public SubredditDto save(SubredditDto subredditDto){
        Subreddit subreddit = subredditRepository.save(getSubreddit(subredditDto));
        List<Post> posts = new ArrayList<>();
        subreddit.setPosts(posts);
        subredditRepository.save(subreddit);

        subredditDto.setId(subreddit.getId());
        subredditDto.setNumberOfPosts(subreddit.getPosts().size());
        return subredditDto;
    }

    private Subreddit getSubreddit(SubredditDto subredditDto){
        User user = authService.getCurrentUser();
        return Subreddit.builder().name(subredditDto.getName())
                .description(subredditDto.getDescription())
                .createdDate(Instant.now())
                .user(user)
                .build();
    }

    private SubredditDto mapSubredditToSubredditDto(Subreddit subreddit){
        List<Post> posts = subreddit.getPosts();
        SubredditDto subredditDto = new SubredditDto(subreddit.getId(), subreddit.getDescription(),
                subreddit.getName(), posts.size());
        return subredditDto;
    }

    @Transactional
    public List<SubredditDto> getAll(){
        List<Subreddit> subreddit = subredditRepository.findAll();
        return subredditRepository.findAll()
                .stream()
                .map(this::mapSubredditToSubredditDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubredditDto getSubredditById(Long id){
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("Subreddit not found"));
        SubredditDto subredditDto = mapSubredditToSubredditDto(subreddit);
        return subredditDto;
    }
}
