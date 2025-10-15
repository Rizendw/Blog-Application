package com.mountblue.blogApplication.controller;

import com.mountblue.blogApplication.dto.TagDto;
import com.mountblue.blogApplication.entity.Tag;
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
    public ResponseEntity<List<Tag>> listTags() {
        List<Tag> tags = tagService.listAllTags();
        List<TagDto> dtos = tags.stream()
                .map(t -> new TagDto(t.getId(), t.getName())).toList();
        return ResponseEntity.ok(tags);
    }

    private record TagDto(Long id, String name) {}
}
