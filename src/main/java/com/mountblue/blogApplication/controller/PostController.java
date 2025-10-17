package com.mountblue.blogApplication.controller;

import com.mountblue.blogApplication.dto.PostRequest;
import com.mountblue.blogApplication.dto.PostResponse;
import com.mountblue.blogApplication.entity.Comment;
import com.mountblue.blogApplication.entity.Tag;
import com.mountblue.blogApplication.entity.User;
import com.mountblue.blogApplication.service.UserService;
import com.mountblue.blogApplication.service.CommentService;
import com.mountblue.blogApplication.service.PostService;
import com.mountblue.blogApplication.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class PostController {

    private final PostService postService;
    private final TagService tagService;
    private final CommentService commentService;
    private final UserService userService;

    // list/search endpoint
    @GetMapping
    public String listPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> tagId,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Boolean published,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDir,
            Model model) {

        Instant from = safeParse(dateFrom);
        Instant to = safeParse(dateTo);

        Page<PostResponse> pageResult = postService.searchPosts(search, tagId, author, published, from, to, page, size, sortField, sortDir);

        model.addAttribute("posts", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("hasNext", pageResult.hasNext());
        model.addAttribute("hasPrev", pageResult.hasPrevious());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());


        List<Tag> allTags = tagService.listAllTags();
        model.addAttribute("allTags", allTags);


        model.addAttribute("search", search);
        model.addAttribute("selectedTagIds", tagId == null ? List.of() : tagId);
        model.addAttribute("authorFilter", author);
        model.addAttribute("isPublished", published);

        return "list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) return "redirect:/login";

        model.addAttribute("postRequest",
                new PostRequest(null, null, "", "", false, Set.of()));
        model.addAttribute("currentUser", currentUser);
        return "/create";
    }

    @PostMapping("/create")
    public String handleCreateForm(@ModelAttribute("createPostRequest") @Valid PostRequest request) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) return "redirect:/login";

        postService.createPost(request);
        return "redirect:/view";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        PostResponse post = postService.getPostById(id);
        List<Comment> comments = commentService.getCommentsByPost(id);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new Comment());
        return "/view";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        PostResponse post = postService.getPostById(id);
        User currentUser = userService.getCurrentUser();

        if (!currentUser.isAdmin() && !post.authorName().equals(currentUser.getName())) {
            throw new AccessDeniedException("Access Denied");
        }

        model.addAttribute("post", post);
        model.addAttribute("currentUser", currentUser);
        return "/create";
    }

    @PostMapping("/edit/{id}")
    public String handleEditForm(@PathVariable Long id, @ModelAttribute @Valid PostRequest request) {
        PostResponse updated = postService.updatePost(id, request);
        //model.addAttribute("post", updated);
        return "redirect:/" + updated.id();
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/";
    }

    private Instant safeParse(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException e) {
            return null; // just ignore bad date
        }
    }
}
