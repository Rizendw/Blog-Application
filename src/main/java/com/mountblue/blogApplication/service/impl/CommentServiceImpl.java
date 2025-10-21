package com.mountblue.blogApplication.service.impl;

import com.mountblue.blogApplication.dto.CommentRequest;
import com.mountblue.blogApplication.entity.Comment;
import com.mountblue.blogApplication.entity.Post;
import com.mountblue.blogApplication.entity.User;
import com.mountblue.blogApplication.repository.CommentRepository;
import com.mountblue.blogApplication.repository.PostRepository;
import com.mountblue.blogApplication.repository.UserRepository;
import com.mountblue.blogApplication.service.CommentService;
import com.mountblue.blogApplication.service.PostService;
import com.mountblue.blogApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final UserService userService;

    @Override
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @Override
    @Transactional
    public Comment addComment(Long postId, CommentRequest commentRequest) {
        User currentUser = userService.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));

        Comment comment = Comment.builder()
                .name(commentRequest.name())
                .email(commentRequest.email())
                .comment(commentRequest.comment())
                .post(post)
                .aUser(currentUser)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return commentRepository.save(comment);
    }


    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public Comment updateComment(Long id, CommentRequest updated) {
        User currentUser = userService.getCurrentUser();
        Comment existing = commentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));

        boolean isAdmin = currentUser != null && Boolean.TRUE.equals(currentUser.getIsAdmin());
        boolean isAuthor = currentUser != null && existing.getAUser() != null
                && Objects.equals(existing.getAUser().getId(), currentUser.getId());

        if (!isAdmin && !isAuthor) {
            throw new AccessDeniedException("You are not authorized to modify this comment");
        }

        existing.setComment(updated.comment());
        existing.setUpdatedAt(Instant.now());
        return commentRepository.save(existing);
    }

    @Override
    public Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public Long deleteComment(Long id) {
        User currentUser = userService.getCurrentUser();

        Comment existing = commentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));

        boolean isAdmin = currentUser != null && Boolean.TRUE.equals(currentUser.getIsAdmin());
        boolean isAuthor = currentUser != null && existing.getAUser() != null
                && Objects.equals(existing.getAUser().getId(), currentUser.getId());

        if (!isAdmin && !isAuthor) {
            throw new AccessDeniedException("You are not authorized to modify this comment");
        }
        Long postId = existing.getPost().getId();
        commentRepository.delete(existing);
        return postId;
    }

    @Override
    @Transactional
    public Comment createCommentApi(Long postId, CommentRequest request) {
        var post = postService.getPostEntity(postId); // you can make a helper in PostServiceImpl if not exist
        var currentUser = userService.getCurrentUser();

        Comment comment = new Comment();
        comment.setName(currentUser != null ? currentUser.getName() : request.name());
        comment.setEmail(currentUser != null ? currentUser.getEmail() : request.email());
        comment.setComment(request.comment());
        comment.setPost(post);

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment updateCommentApi(Long postId, Long commentId, CommentRequest request) {
        var post = postService.getPostEntity(postId);
        var currentUser = userService.getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // ensure comment belongs to this post
        if (!comment.getPost().getId().equals(postId))
            throw new AccessDeniedException("Comment does not belong to this post");

        boolean isOwner = currentUser != null &&
                comment.getEmail().equals(currentUser.getEmail());

        if (!isOwner && (currentUser == null || !currentUser.getIsAdmin()))
            throw new AccessDeniedException("Not allowed to edit this comment");

        comment.setComment(request.comment());
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteCommentApi(Long postId, Long commentId) {
        var post = postService.getPostEntity(postId);
        var currentUser = userService.getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getPost().getId().equals(postId))
            throw new AccessDeniedException("Comment does not belong to this post");

        boolean isOwner = currentUser != null &&
                comment.getEmail().equals(currentUser.getEmail());

        if (!isOwner && (currentUser == null || !currentUser.getIsAdmin()))
            throw new AccessDeniedException("Not allowed to delete this comment");

        commentRepository.delete(comment);
    }

}
