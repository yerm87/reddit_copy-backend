package com.example.reddit.mappers;

public class SubredditDto {
    private Long id;
    private String description;
    private String name;
    private int numberOfPosts;

    public SubredditDto(Long id, String description, String name, int numberOfPosts) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.numberOfPosts = numberOfPosts;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Long getId(){
        return id;
    }

    public int getNumberOfPosts() {
        return numberOfPosts;
    }

    public void setNumberOfPosts(int numberOfPosts){
        this.numberOfPosts = numberOfPosts;
    }
}
