package com.mountblue.blogApplication.service;

import com.mountblue.blogApplication.entity.Comment;
import java.util.List;


public interface CommentService {
    List<Comment> getCommentsByPost(Long postId);
    Comment addComment(Long postId, Comment comment);
    Comment updateComment(Long id, Comment updated);
    Comment getById(Long id);

//    CommentDto createComment(CommentDto dto);
//    CommentDto updateComment(Long id, CommentDto dto);
//    Optional<CommentDto> getComment(Long id);
      Long deleteComment(Long id);
//    List<CommentDto> listApprovedCommentsForPost(Long postId);
}
