package com.example.reddit.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message="Username is required")
    private String username;
    @NotBlank(message="Password is required")
    private String password;
    @Email
    @NotEmpty(message="Email is required")
    private String email;
    private Instant createdAt;
    private boolean enabled;
    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private List<Comment> comments;
    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private List<Post> posts;
    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private List<Vote> votes;

    public String toString(){
        return "user with: id " + id + " and username: " + username;
    }
}
