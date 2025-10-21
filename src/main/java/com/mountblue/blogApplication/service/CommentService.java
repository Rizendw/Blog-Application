package com.mountblue.blogApplication.service;

import com.mountblue.blogApplication.dto.CommentRequest;
import com.mountblue.blogApplication.entity.Comment;

import java.util.List;


public interface CommentService {
    List<Comment> getCommentsByPost(Long postId);
    Comment addComment(Long postId, CommentRequest comment);
    Comment updateComment(Long id, CommentRequest updated);
    Comment getById(Long id);
    Long deleteComment(Long id);
    Comment createCommentApi(Long postId, CommentRequest request);
    Comment updateCommentApi(Long postId, Long commentId, CommentRequest request);
    void deleteCommentApi(Long postId, Long commentId);

}
