package com.mountblue.blogApplication.restcontroller;

import com.mountblue.blogApplication.dto.CommentRequest;
import com.mountblue.blogApplication.entity.Comment;
import com.mountblue.blogApplication.repository.CommentRepository;
import com.mountblue.blogApplication.response.ApiResponse;
import com.mountblue.blogApplication.service.CommentService;
import com.mountblue.blogApplication.service.PostService;
import com.mountblue.blogApplication.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;


    @GetMapping
    public ResponseEntity<ApiResponse<List<Comment>>> listComments(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Comments fetched successfully", comments));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Comment>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request) {

        Comment saved = commentService.createCommentApi(postId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Comment created successfully", saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authz.isPostOwnerOrAdmin(#id)")
    public ResponseEntity<ApiResponse<Comment>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request) {

        Comment updated = commentService.updateCommentApi(postId, id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Comment updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authz.isPostOwnerOrAdmin(#id)")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long id) {

        commentService.deleteCommentApi(postId, id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Comment deleted successfully", null));
    }
}
