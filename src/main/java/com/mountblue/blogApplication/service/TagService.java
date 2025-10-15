package com.mountblue.blogApplication.service;

import com.mountblue.blogApplication.dto.TagDto;
import com.mountblue.blogApplication.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {
    TagDto createTag(TagDto dto);
    Optional<TagDto> getTagById(Long id);
    List<Tag> listAllTags();
}
