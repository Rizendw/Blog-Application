package com.mountblue.blogApplication.security;

import com.mountblue.blogApplication.entity.Comment;
import com.mountblue.blogApplication.entity.Post;
import com.mountblue.blogApplication.entity.User;
import com.mountblue.blogApplication.repository.CommentRepository;
import com.mountblue.blogApplication.repository.PostRepository;
import com.mountblue.blogApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component("authz")
@RequiredArgsConstructor
public class AuthzService {

    private final UserService userService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public boolean isPostOwnerOrAdmin(Long postId) {
        User current = userService.getCurrentUser();
        if (current == null)
            return false;

        if (current.getIsAdmin())
            return true;

        Post post = postRepository.findById(postId).orElseThrow(() -> new AccessDeniedException("Post not found"));
        return post.getAUser() != null && post.getAUser().getId().equals(current.getId());
    }

    public boolean isCommentOwnerOrAdmin(Long commentId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null)
            return false;

        if (currentUser.getIsAdmin())
            return true;

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AccessDeniedException("Comment not found"));
        return comment.getAUser() != null && comment.getAUser().getId().equals(currentUser.getId());
    }
}
