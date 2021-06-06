package com.example.reddit.services;

import com.example.reddit.entities.Comment;
import com.example.reddit.entities.NotificationEmail;
import com.example.reddit.entities.Post;
import com.example.reddit.entities.User;
import com.example.reddit.exceptions.SpringRedditException;
import com.example.reddit.mappers.CommentDto;
import com.example.reddit.repos.CommentRepository;
import com.example.reddit.repos.PostRepository;
import com.example.reddit.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final PostRepository postRepository;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(PostRepository postRepository, AuthService authService, UserRepository userRepository,
                          MailService mailService, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.authService = authService;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public void createComment(CommentDto commentDto){
        Comment comment = generateCommentFromCommentDto(commentDto);

        Post post = addCommentsToPost(commentDto.getPostId(), comment);

        User user = addCommentsToUser(comment);

        sendEmail(post, user);
    }

    private Comment generateCommentFromCommentDto(CommentDto commentDto){
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setCreatedDate(Instant.now());
        commentRepository.save(comment);

        return comment;
    }

    private Post addCommentsToPost(Long postId, Comment comment){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SpringRedditException("Post with id: " + postId
                        + " doesn't exist"));

        List<Comment> comments = post.getComments();
        comments.add(comment);
        post.setComments(comments);
        postRepository.save(post);

        return post;
    }

    private User addCommentsToUser(Comment comment){
        User user = authService.getCurrentUser();
        List<Comment> commentsFromUser = user.getComments();
        commentsFromUser.add(comment);
        user.setComments(commentsFromUser);
        userRepository.save(user);

        return user;
    }

    private void sendEmail(Post post, User user){
        NotificationEmail notificationEmail = new NotificationEmail();
        notificationEmail.setSubject("your post was commented");
        notificationEmail.setRecipient(post.getUser().getEmail());
        notificationEmail.setBody("Your post was commented by: " + user.getUsername());

        mailService.sendMail(notificationEmail);
    }

    @Transactional
    public List<CommentDto> getAllCommentsByPostId(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new SpringRedditException("Post with id: " + postId + " doesn't exist"));
        List<Comment> comments = post.getComments();
        return comments.stream()
                        .map(comment -> mapCommentToCommentDto(comment))
                        .collect(Collectors.toList());
    }

    private CommentDto mapCommentToCommentDto(Comment comment){
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setPostId(comment.getPost().getId());
        commentDto.setCreateAt(comment.getCreatedDate());
        commentDto.setText(comment.getText());
        commentDto.setUsername(comment.getUser().getUsername());

        return commentDto;
    }

    @Transactional
    public List<CommentDto> getCommentsByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SpringRedditException("user with username: " + username + "doesn't exist"));
        return user.getComments().stream()
                .map(comment -> mapCommentToCommentDto(comment))
                .collect(Collectors.toList());
    }
}
