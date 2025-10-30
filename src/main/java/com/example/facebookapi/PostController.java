package com.example.facebookapi;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // Create a new post
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post, UriComponentsBuilder uriBuilder) {
        // id and timestamps are managed by JPA lifecycle callbacks
        Post saved = postRepository.save(post);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriBuilder.path("/api/posts/{id}").buildAndExpand(saved.getId()).toUri());
        return new ResponseEntity<>(saved, headers, HttpStatus.CREATED);
    }

    // Get all posts
    @GetMapping
    public List<Post> listPosts() {
        return postRepository.findAll();
    }

    // Get a single post by id
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Optional<Post> p = postRepository.findById(id);
        return p.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update a post (replace fields: author, message, imageUrl)
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post incoming) {
        return postRepository.findById(id).map(existing -> {
            // update fields if provided (simple replacement)
            existing.setAuthor(incoming.getAuthor());
            existing.setMessage(incoming.getMessage());
            existing.setImageUrl(incoming.getImageUrl());
            Post updated = postRepository.save(existing);
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        return postRepository.findById(id).map(existing -> {
            postRepository.deleteById(id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}