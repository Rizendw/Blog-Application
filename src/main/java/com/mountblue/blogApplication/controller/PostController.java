package com.mountblue.blogApplication.controller;

import com.mountblue.blogApplication.dto.CreatePostRequest;
import com.mountblue.blogApplication.dto.PostResponse;
import com.mountblue.blogApplication.entity.Comment;
import com.mountblue.blogApplication.entity.Tag;
import com.mountblue.blogApplication.service.CommentService;
import com.mountblue.blogApplication.service.PostService;
import com.mountblue.blogApplication.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class PostController {

    private final PostService postService;
    private final TagService tagService;
    private final CommentService commentService;

//    @GetMapping
//    public String listPosts(@RequestParam(defaultValue = "0") int page,
//                            @RequestParam(defaultValue = "10") int size,
//                            Model model) {
//
//        Page<PostResponse> posts = postService.getPaginatedPosts(page, size);
//        model.addAttribute("posts", posts.getContent());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("hasNext", posts.hasNext());
//        model.addAttribute("hasPrev", posts.hasPrevious());
//        return "/list";
//    }

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

        Instant from = null;
        Instant to = null;
        try {
            if (dateFrom != null && !dateFrom.isBlank()) from = Instant.parse(dateFrom);
            if (dateTo != null && !dateTo.isBlank()) to = Instant.parse(dateTo);
        } catch (DateTimeParseException e) {
            return "redirect:/";
        }

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

        return "/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createPostRequest", new CreatePostRequest("", "", "", java.util.List.of()));
        return "/create";
    }

    @PostMapping("/create")
    public String handleCreateForm(@ModelAttribute("createPostRequest") @Valid CreatePostRequest request, Model model) {
        PostResponse created = postService.createPost(request);
        model.addAttribute("post", created);
        return "/view";
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
        CreatePostRequest dto = new CreatePostRequest(post.title(), post.content(), post.author(), post.tags());
        model.addAttribute("createPostRequest", dto);
        model.addAttribute("postId", id);
        return "/create";
    }

    @PostMapping("/edit/{id}")
    public String handleEditForm(@PathVariable Long id, @ModelAttribute("createPostRequest") @Valid CreatePostRequest request, Model model) {
        PostResponse updated = postService.updatePost(id, request);
        //model.addAttribute("post", updated);
        return "redirect:/" + updated.id();
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/";
    }

//    @GetMapping
//    public String listAllPosts(
//        @RequestParam(value = "start", required = false, defaultValue = "1")
//        int start,
//        @RequestParam(value = "limit", required = false, defaultValue = "10")
//        int limit,
//        @RequestParam(value = "sortField", required = false, defaultValue = "publishedAt")
//        String sortField,
//        @RequestParam(value = "order", required = false, defaultValue = "desc")
//        String order,
//        @RequestParam(value = "tagMode", required = false, defaultValue = "OR")
//        String tagMode,
//        @RequestParam(value = "tagId", required = false)
//        List<Long> tagIds,
//        @RequestParam(value = "authorId", required = false)
//        Long authorId,
//        @RequestParam(value = "search", required = false)
//        String search,
//        Model model){
//
//            int offset = Math.max(1, start) - 1;
//            int pageNumber = offset / Math.max(1, limit);
//            PageRequest pageable = PageRequest.of(pageNumber, limit);
//
//            Page<PostDto> page = postService.searchPosts(search, authorId, tagIds, tagMode, sortField, order, pageable);
//            model.addAttribute("posts", page.getContent());
//            model.addAttribute("total", page.getTotalElements());
//            model.addAttribute("pageNumber", pageNumber);
//            model.addAttribute("pageSize", limit);
//            model.addAttribute("totalPages", page.getTotalPages());
//            model.addAttribute("currentStart", start);
//            model.addAttribute("allTags", tagService.listAllTags());
//            model.addAttribute("search", search);
//            model.addAttribute("tagIds", tagIds);
//            model.addAttribute("tagMode", tagMode);
//            model.addAttribute("sortField", sortField);
//            model.addAttribute("order", order);
//
//            return "posts/list";
//        }
//
//    @GetMapping("/new")
//    public String newPostForm(Model model) {
//        model.addAttribute("postDto", new PostDto());
//        model.addAttribute("allTags", tagService.listAllTags());
//        return "posts/form";
//    }
//
//    @PostMapping
//    public String createPost(@ModelAttribute("postDto") @Valid PostDto postDto, BindingResult bindingResult, Model model) {
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("allTags", tagService.listAllTags());
//            return "posts/form";
//        }
//        PostDto saved = postService.createPost(postDto);
//        return "redirect:/posts/" + saved.getId();
//    }
//
//    @GetMapping("/{id}")
//    public String showPost(@PathVariable("id") Long id, Model model){
//        PostDto dto = postService.getPostById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not Found"));
//        model.addAttribute("post", dto);
//
//        List<CommentDto> comments = commentService.listApprovedCommentsForPost(id);
//        model.addAttribute("comments", comments);
//        model.addAttribute("newComment", new CommentDto());
//        return "posts/detail";
//    }
//
//    @GetMapping("/{id}/edit")
//    public String editPostForm(@PathVariable Long id, Model model) {
//        var opt = postService.getPostById(id);
//        if(opt.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
//        }
//        PostDto dto = opt.get();
//        dto.setTags(String.join(",", dto.getTagList() != null ? dto.getTagList() : List.of()));
//        model.addAttribute("postDto", dto);
//        model.addAttribute("allTags", tagService.listAllTags());
//
//        return "posts/form";
//    }
//
//    @PostMapping("/{id}/edit")
//    public String updatePost(@PathVariable Long id, @ModelAttribute("postDto") @Valid PostDto postDto, BindingResult bindingResult, Model model){
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("allTags", tagService.listAllTags());
//            return "posts/form";
//        }
//        PostDto updated = postService.updatePost(id, postDto);
//        return "redirect:/posts/" + updated.getId();
//    }
//

}
