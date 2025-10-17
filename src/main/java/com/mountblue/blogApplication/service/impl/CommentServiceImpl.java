package com.mountblue.blogApplication.service.impl;

import com.mountblue.blogApplication.dto.CommentRequest;
import com.mountblue.blogApplication.entity.Comment;
import com.mountblue.blogApplication.entity.Post;
import com.mountblue.blogApplication.entity.User;
import com.mountblue.blogApplication.repository.CommentRepository;
import com.mountblue.blogApplication.repository.PostRepository;
import com.mountblue.blogApplication.service.CommentService;
import com.mountblue.blogApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Override
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @Override
    @Transactional
    public void addComment(Long postId, CommentRequest commentRequest) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Login required to comment");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
        System.err.println("aa ya aa gy!!!!!a");
        Comment comment = new Comment();
        comment.setUser(currentUser);
        comment.setComment(commentRequest.comment());
        comment.setPost(post);
        commentRepository.save(comment);
    }


    @Override
    @Transactional
    public void updateComment(Long id, CommentRequest updated) {
        User currentUser = userService.getCurrentUser();
        Comment existing = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!currentUser.isAdmin() && !existing.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Cannot modify another user’s comment");
        }
        commentRepository.save(existing);
    }

    @Override
    public Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }

    @Override
    @Transactional
    public Long deleteComment(Long id) {
        User currentUser = userService.getCurrentUser();

        Comment existing = commentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));
        Long postId = existing.getPost().getId();
        if (!currentUser.isAdmin() && !existing.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Cannot modify another user’s comment");
        }
        commentRepository.delete(existing);
        return postId;
    }


}
