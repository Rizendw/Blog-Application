package com.mountblue.blogApplication.service.impl;

import com.mountblue.blogApplication.dto.TagDto;
import com.mountblue.blogApplication.entity.Tag;
import com.mountblue.blogApplication.repository.TagRepository;
import com.mountblue.blogApplication.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public TagDto createTag(TagDto dto) {
        Tag saved = tagRepository.save(Tag.builder().name(dto.getName()).build());
        return map(saved);
    }

    @Override
    public Optional<TagDto> getTagById(Long id) {
        return tagRepository.findById(id).map(this::map);
    }

    @Override
    public List<Tag> listAllTags() {
        List<Tag> tags = tagRepository.findAll();
        tags.sort(Comparator.comparing(Tag::getName, String.CASE_INSENSITIVE_ORDER));
        return tags;
    }

    private TagDto map(Tag t) {
        return TagDto.builder().id(t.getId()).name(t.getName()).build();
    }
}
