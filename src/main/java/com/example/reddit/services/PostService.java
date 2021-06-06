package com.example.reddit.services;

import com.example.reddit.entities.*;
import com.example.reddit.exceptions.SpringRedditException;
import com.example.reddit.mappers.PostRequest;
import com.example.reddit.mappers.PostResponse;
import com.example.reddit.repos.PostRepository;
import com.example.reddit.repos.SubredditRepository;
import com.example.reddit.repos.UserRepository;
import com.example.reddit.repos.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final SubredditRepository subredditRepository;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    @Autowired
    public PostService(PostRepository postRepository, SubredditRepository subredditRepository,
                       AuthService authService, UserRepository userRepository, VoteRepository voteRepository) {
        this.postRepository = postRepository;
        this.subredditRepository = subredditRepository;
        this.authService = authService;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }

    @Transactional
    public PostResponse createPost(PostRequest postRequest){
        Subreddit subreddit = getSubredditByName(postRequest.getSubredditName());
        Post post = getPostFromPostRequest((postRequest));

        List<Post> posts = subreddit.getPosts();
        posts.add(post);
        subreddit.setPosts(posts);
        subredditRepository.save(subreddit);

        User user = addPostToUser(post);

        return generatePostResponse(post, subreddit, user);
    }

    private Subreddit getSubredditByName(String name){
        Subreddit subreddit = subredditRepository.findByName(name)
                .orElseThrow(() -> new SpringRedditException("There is no subreddit with such name"));
        return subreddit;
    }

    private Post getPostFromPostRequest(PostRequest postRequest){
        List<Comment> comments = new ArrayList<>();
        List<Vote> votes = new ArrayList<>();

        Post post = new Post();
        post.setPostName(postRequest.getPostName());
        post.setDescription(postRequest.getDescription());
        post.setUrl(postRequest.getUrl());
        post.setCreatedDate(Instant.now());
        post.setComments(comments);
        post.setVotes(votes);

        Post postEntity = postRepository.save(post);
        return postEntity;
    }

    private User addPostToUser(Post post){
        User user = authService.getCurrentUser();
        List<Post> posts = user.getPosts();
        posts.add(post);
        user.setPosts(posts);
        userRepository.save(user);

        return user;
    }

    private PostResponse generatePostResponse(Post post, Subreddit subreddit, User user){
        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());
        postResponse.setDescription(post.getDescription());
        postResponse.setPostName(post.getPostName());
        postResponse.setUrl(post.getUrl());
        postResponse.setSubredditName(subreddit.getName());
        postResponse.setUsername(user.getUsername());
        postResponse.setCommentCount(post.getComments().size());
        postResponse.setVoteCount(post.getVoteCount());

        User currentUser = authService.getCurrentUser();

        Optional<Vote> vote = voteRepository.findTopByPostAndUserOrderByIdDesc(post, currentUser);
        if(vote.isPresent()){
            if(vote.get().getVoteType() == VoteType.UPVOTE){
                postResponse.setUpVote(true);
                postResponse.setDownVote(false);
            } else {
                postResponse.setUpVote(false);
                postResponse.setDownVote(true);
            }
        } else {
            postResponse.setUpVote(false);
            postResponse.setDownVote(false);
        }

        return postResponse;
    }

    @Transactional
    public List<PostResponse> getAllPosts(){
        List<Post> posts = postRepository.findAll();
        List<PostResponse> postResponses = posts.stream()
                .map(post -> {
                   Subreddit subreddit = post.getSubreddit();
                   User user = post.getUser();
                   return generatePostResponse(post, subreddit, user);
                }).collect(Collectors.toList());
        return postResponses;
    }

    @Transactional
    public PostResponse getPostById(Long id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("post with id: " + id + " doesn't exist"));
        Subreddit subreddit = post.getSubreddit();
        User user = post.getUser();
        return generatePostResponse(post, subreddit, user);
    }

    @Transactional
    public List<PostResponse> getPostsBySubredditId(Long id){
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("Subreddit with id: " + id + " doesn't exist"));
        List<Post> posts = subreddit.getPosts();
        List<PostResponse> postResponses = posts.stream()
                .map(post -> {
                    User user = post.getUser();
                    return generatePostResponse(post, subreddit, user);
                }).collect(Collectors.toList());
        return postResponses;
    }

    @Transactional
    public List<PostResponse> getPostsByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SpringRedditException("user doesn't exist"));
        List<Post> posts = user.getPosts();
        List<PostResponse> postResponses = posts.stream()
                .map(post -> {
                    Subreddit subreddit = post.getSubreddit();
                    return generatePostResponse(post, subreddit, user);
                }).collect(Collectors.toList());
        return postResponses;
    }
}
