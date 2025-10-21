package com.mountblue.blogApplication.restcontroller;

import com.mountblue.blogApplication.dto.TagRequest;
import com.mountblue.blogApplication.entity.Tag;
import com.mountblue.blogApplication.response.ApiResponse;
import com.mountblue.blogApplication.service.TagService;
import com.mountblue.blogApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagRestController {

    private final TagService tagService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TagRequest>>> listTags() {
        List<TagRequest> tags = tagService.listAllTags();
        return ResponseEntity.ok(new ApiResponse<>(true, "Tags fetched successfully", tags));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TagRequest>> getTag(@PathVariable Long id) {
        TagRequest tag = tagService.getTagByIdApi(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tag fetched successfully", tag));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TagRequest>> createTag(@RequestBody TagRequest request) {
        var currentUser = userService.getCurrentUser();
        if (currentUser == null || !currentUser.getIsAdmin()) {
            throw new AccessDeniedException("Only admins can create tags");
        }

        TagRequest created = tagService.createTagApi(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Tag created successfully", created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTag(@PathVariable Long id) {
        var currentUser = userService.getCurrentUser();
        if (currentUser == null || !currentUser.getIsAdmin()) {
            throw new AccessDeniedException("Only admins can delete tags");
        }

        tagService.deleteTagApi(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tag deleted successfully", null));
    }
}
