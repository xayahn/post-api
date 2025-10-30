package com.example.facebookapi;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    // standard CRUD from JpaRepository is sufficient for this simple API
}