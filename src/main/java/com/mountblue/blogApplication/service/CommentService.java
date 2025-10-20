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

}
