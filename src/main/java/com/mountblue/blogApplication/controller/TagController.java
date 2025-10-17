package com.mountblue.blogApplication.controller;


import com.mountblue.blogApplication.dto.TagRequest;
import com.mountblue.blogApplication.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {
    private final TagService tagService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<TagRequest>>listTags() {
        List<TagRequest> tags = tagService.listAllTags();
        return ResponseEntity.ok(tags);
    }
}
