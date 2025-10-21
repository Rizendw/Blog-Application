package com.mountblue.blogApplication.service;

import com.mountblue.blogApplication.dto.TagRequest;

import java.util.List;
import java.util.Optional;

public interface TagService {
    TagRequest createTag(TagRequest dto);
    Optional<TagRequest> getTagById(Long id);
    List<TagRequest> listAllTags();
    TagRequest getTagByIdApi(Long id);
    TagRequest createTagApi(TagRequest request);
    void deleteTagApi(Long id);
}
