package com.example.reddit.services;

import com.example.reddit.entities.Post;
import com.example.reddit.entities.User;
import com.example.reddit.entities.Vote;
import com.example.reddit.entities.VoteType;
import com.example.reddit.exceptions.SpringRedditException;
import com.example.reddit.mappers.VoteRequest;
import com.example.reddit.repos.PostRepository;
import com.example.reddit.repos.UserRepository;
import com.example.reddit.repos.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VoteService {
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Autowired
    public VoteService(VoteRepository voteRepository, PostRepository postRepository, AuthService authService,
                       UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.postRepository = postRepository;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void vote(VoteRequest voteRequest){
        Post post = postRepository.findById(voteRequest.getPostId())
                .orElseThrow(() -> new SpringRedditException("Post with id: " + voteRequest.getPostId() +
                        " doesn't exist"));
        Optional<Vote> postVote = voteRepository.findTopByPostAndUserOrderByIdDesc(post, authService.getCurrentUser());

        if(postVote.isPresent()){
            if(voteRequest.getVoteType().equals(postVote.get().getVoteType())){
                throw new SpringRedditException("User already voted");
            }
        }

        if(voteRequest.getVoteType().equals(VoteType.UPVOTE)){
            post.setVoteCount(post.getVoteCount() + 1);
        } else {
            post.setVoteCount(post.getVoteCount() - 1);
        }

        Vote generatedVote = generateOrUpdateVote(voteRequest, authService.getCurrentUser(), post);

        addVoteToPost(post, generatedVote);
        addVoteToUser(authService.getCurrentUser(), generatedVote);

        postRepository.save(post);
        userRepository.save(authService.getCurrentUser());
    }

    private Vote generateOrUpdateVote(VoteRequest voteRequest, User user, Post post){
        Optional<Vote> optionalVote = voteRepository.findTopByPostAndUserOrderByIdDesc(post, user);
        Vote vote = null;

        if(optionalVote.isPresent()){
            vote = optionalVote.get();
            vote.setVoteType(voteRequest.getVoteType());
        } else {
            vote = new Vote();
            vote.setVoteType(voteRequest.getVoteType());
        }
        voteRepository.save(vote);
        return vote;
    }

    private void addVoteToPost(Post post, Vote vote){
        List<Vote> votes = post.getVotes();
        if(votes.contains(vote)){
            return;
        }
        votes.add(vote);
        post.setVotes(votes);
    }

    private void addVoteToUser(User user, Vote vote){
        List<Vote> votes = user.getVotes();
        if(votes.contains(vote)){
            return;
        }
        votes.add(vote);
        user.setVotes(votes);
    }
}
