package com.mountblue.blogApplication.controller;

import com.mountblue.blogApplication.dto.CommentRequest;
import com.mountblue.blogApplication.entity.Comment;
import com.mountblue.blogApplication.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
                             @ModelAttribute @Valid CommentRequest commentRequest,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Invalid data");
            return "redirect:/view" + postId;
        }

        commentService.addComment(postId, commentRequest);
        return "redirect:/" + postId;
    }

    @PostMapping("/{id}/edit")
    public String updateComment(@PathVariable Long id,
                                @ModelAttribute @Valid CommentRequest comment,
                                BindingResult bindingResult,
                                Model model) {
        Comment existing = commentService.getById(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Invalid input");
            return "redirect:/" + existing.getPost().getId();
        }
        commentService.updateComment(existing.getId(), comment);
        return "redirect:/" + existing.getPost().getId();
    }

    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable Long id) {
        Long postId = commentService.deleteComment(id);
        return "redirect:/" + postId;
    }
}
