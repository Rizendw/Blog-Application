package com.mountblue.blogApplication.restcontroller;

import com.mountblue.blogApplication.dto.PostRequest;
import com.mountblue.blogApplication.dto.PostResponse;
import com.mountblue.blogApplication.response.ApiResponse;
import com.mountblue.blogApplication.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostRestController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponse>>> listPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> tagId,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Boolean published,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDir
    ) {
        Instant from = parse(dateFrom);
        Instant to = parse(dateTo);

        Page<PostResponse> pageResult = postService.searchPosts(
                search, tagId, author, published, from, to, page, size, sortField, sortDir);

        return  ResponseEntity.ok(new ApiResponse<>
                (true, "Post fetched successfully", pageResult));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long id) {
        PostResponse post = postService.getPostById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Post fetched successfully", post));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@Valid @RequestBody PostRequest postRequest) {
        PostResponse created = postService.createPost(postRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Post created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authz.isPostOwnerOrAdmin(#id)")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest postRequest) {

        PostResponse updated = postService.updatePost(id, postRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Post updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authz.isPostOwnerOrAdmin(#id)")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Post deleted successfully", null));
    }

    private Instant parse(String value) {
        try {
            return (value == null || value.isBlank()) ? null : Instant.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
