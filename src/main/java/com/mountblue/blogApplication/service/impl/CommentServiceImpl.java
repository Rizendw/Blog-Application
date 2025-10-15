package com.mountblue.blogApplication.service.impl;

import com.mountblue.blogApplication.dto.CommentRequest;
import com.mountblue.blogApplication.entity.Comment;
import com.mountblue.blogApplication.entity.Post;
import com.mountblue.blogApplication.repository.CommentRepository;
import com.mountblue.blogApplication.repository.PostRepository;
import com.mountblue.blogApplication.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @Override
    @Transactional
    public void addComment(Long postId, com.mountblue.blogApplication.dto.CommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
        System.err.println("aa ya aa gy!!!!!a");
        Comment comment = new Comment();
        comment.setName(request.name());
        comment.setEmail(request.email());
        comment.setComment(request.comment());
        comment.setPost(post);
        commentRepository.save(comment);
    }


    @Override
    @Transactional
    public void updateComment(Long id, CommentRequest updated) {
        Comment existing = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        existing.setName(updated.name());
        existing.setEmail(updated.email());
        existing.setComment(updated.comment());
        //return commentRepository.save(existing);
    }

    @Override
    public Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }

    @Override
    @Transactional
    public Long deleteComment(Long id) {
        Comment existing = commentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));
        Long postId = existing.getPost().getId();
        commentRepository.delete(existing);
        return postId;
    }

//    @Override
//    public CommentDto createComment(CommentDto dto) {
//        Post post = postRepository.findById(dto.getPostId())
//                .orElseThrow(() -> new NoSuchElementException("Post not found"));
//        Comment comment = Comment.builder()
//                .name(dto.getName())
//                .email(dto.getEmail())
//                .comment(dto.getComment())
//                .post(post)
//                .isApproved(dto.isApproved())
//                .build();
//        Comment saved = commentRepository.save(comment);
//        return map(saved);
//    }
//
//
//    @Override
//    public CommentDto updateComment(Long id, CommentDto dto) {
//        Comment comment = commentRepository.findById(id)
//                .orElseThrow(() -> new NoSuchElementException("Comment not found"));
//        comment.setName(dto.getName());
//        comment.setEmail(dto.getEmail());
//        comment.setComment(dto.getComment());
//        comment.setApproved(dto.isApproved());
//        return map(commentRepository.save(comment));
//    }
//
//    @Override
//    public Optional<CommentDto> getComment(Long id) {
//        return commentRepository.findById(id).map(this::map);
//    }
//
//
//    @Override
//    public List<CommentDto> listApprovedCommentsForPost(Long postId) {
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new NoSuchElementException("Post not found"));
//
//        return commentRepository.findByPostId(post)
//                .stream().map(this::map)
//                .collect(Collectors.toList());
//    }
//
//    private CommentDto map(Comment comment) {
//        return CommentDto.builder()
//                .id(comment.getId())
//                .name(comment.getName())
//                .email(comment.getEmail())
//                .comment(comment.getComment())
//                .postId(comment.getPost() != null ? comment.getId() : null)
//                .isApproved(comment.isApproved())
//                .build();
//    }
}
