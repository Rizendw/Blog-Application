package com.mountblue.blogApplication.service;

import com.mountblue.blogApplication.dto.PostRequest;
import com.mountblue.blogApplication.dto.PostResponse;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;

public interface PostService {
    PostResponse createPost(PostRequest request);
    Page<PostResponse> getPaginatedPosts(int page, int size);
    PostResponse getPostById(Long id);
    PostResponse updatePost(Long id, PostRequest request);
    void deletePost(Long id);
    Page<PostResponse> searchPosts(String search, List<Long> tagId, String author, Boolean isPublished, Instant from, Instant to, int page, int size, String sortField, String sortDir);
}
