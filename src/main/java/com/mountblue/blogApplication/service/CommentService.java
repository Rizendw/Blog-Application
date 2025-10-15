package com.mountblue.blogApplication.service;

import com.mountblue.blogApplication.dto.CommentRequest;
import com.mountblue.blogApplication.entity.Comment;

import java.util.List;


public interface CommentService {
    List<Comment> getCommentsByPost(Long postId);
    void addComment(Long postId, CommentRequest comment);
    void updateComment(Long id, CommentRequest updated);
    Comment getById(Long id);
    Long deleteComment(Long id);

//    CommentDto createComment(CommentDto dto);
//    CommentDto updateComment(Long id, CommentDto dto);
//    Optional<CommentDto> getComment(Long id);
//    List<CommentDto> listApprovedCommentsForPost(Long postId);
}
