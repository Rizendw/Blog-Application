package com.mountblue.blogApplication.controller;

import com.mountblue.blogApplication.entity.Comment;
import com.mountblue.blogApplication.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/add/{postId}")
    public String addComment(@PathVariable Long postId, @ModelAttribute @Valid Comment comment) {
        System.err.println("caled!!controler  called!!");
        commentService.addComment(postId, comment);
        return "redirect:/" + postId;
    }

    @PostMapping("/{id}/edit")
    public String updateComment(@PathVariable Long id, @ModelAttribute @Valid Comment comment) {
        Comment existing = commentService.getById(id);
        commentService.updateComment(existing.getId(), comment);
        return "redirect:/" + existing.getPost().getId();
    }

    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable Long id) {
        Long postId = commentService.deleteComment(id);
        return "redirect:/" + postId;
    }
}
