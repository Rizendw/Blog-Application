package com.mountblue.blogApplication.controller;

import com.mountblue.blogApplication.dto.CommentRequest;
import com.mountblue.blogApplication.entity.Comment;
import com.mountblue.blogApplication.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/add/{postId}")
    public String addComment(@PathVariable Long postId,
                             @ModelAttribute("comment") @Valid CommentRequest commentRequest,
                             Model model) {
            commentService.addComment(postId, commentRequest);
            return "redirect:/" + postId + "?commentSuccess";//review this
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("@authz.isCommentOwnerOrAdmin(#id)")
    public String updateComment(@PathVariable Long id,
                                @ModelAttribute("comment") @Valid CommentRequest commentRequest,
                                Model model) {
        commentService.updateComment(id, commentRequest);
        return "redirect:/";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("@authz.isCommentOwnerOrAdmin(#id)")
    public String deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return "redirect:/";
    }
}
