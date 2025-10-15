package com.mountblue.blogApplication.service;

import com.mountblue.blogApplication.dto.CreatePostRequest;
import com.mountblue.blogApplication.dto.PostResponse;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;

public interface PostService {
    PostResponse createPost(CreatePostRequest request);
    Page<PostResponse> getPaginatedPosts(int page, int size);
    PostResponse getPostById(Long id);
    PostResponse updatePost(Long id, CreatePostRequest request);
    void deletePost(Long id);
    Page<PostResponse> searchPosts(String search, List<Long> tagId, String author, Boolean isPublished, Instant from, Instant to, int page, int size, String sortField, String sortDir);


    /**
     * Search and filter posts.
     *
     * @param q text search (OR across title/content/author/tags)
     * @param tagIds list of tag ids (any of)
     * @param author author substring filter
     * @param isPublished published filter (nullable)
     * @param dateFrom createdAt >= dateFrom (nullable)
     * @param dateTo createdAt <= dateTo (nullable)
     * @param page 0-based page
     * @param size page size
     * @param sortField sort by field (default updatedAt)
     * @param sortDir asc|desc
     */

//    PostDto createPost(PostDto dto);
//    PostDto updatePost(Long id, PostDto dto);
//    Optional<PostDto> getPostById(Long id);
//    List<PostDto> listAllPosts();
}
