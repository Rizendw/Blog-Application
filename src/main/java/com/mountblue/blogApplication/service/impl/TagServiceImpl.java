package com.mountblue.blogApplication.service.impl;

import com.mountblue.blogApplication.dto.TagRequest;
import com.mountblue.blogApplication.entity.Tag;
import com.mountblue.blogApplication.repository.TagRepository;
import com.mountblue.blogApplication.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public TagRequest createTag(TagRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Tag name cannot be empty");
        }

        String normalized = request.name().trim();
        String nameLower = normalized.toLowerCase(Locale.ROOT);

        Optional<Tag> existing = tagRepository.findByNameLower(nameLower);
        Tag saved;
        if (existing.isPresent()) {
            saved = existing.get();
        } else {
            Tag tag = Tag.builder()
                    .name(normalized)
                    .nameLower(nameLower)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            saved = tagRepository.save(tag);
        }

        return map(saved);
    }

    @Override
    public Optional<TagRequest> getTagById(Long id) {
        return tagRepository.findById(id).map(this::map);
    }

    @Override
    public List<TagRequest> listAllTags() {
        List<Tag> tags = tagRepository.findAll();
        tags.sort(Comparator.comparing(Tag::getName, String.CASE_INSENSITIVE_ORDER));
        return tags
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public TagRequest getTagByIdApi(Long id) {
        return getTagById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

    }

    private TagRequest map(Tag tag) {
        return new TagRequest(tag.getId(), tag.getName());
    }

    public Tag getTagEntityById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tag not found"));
    }


    @Override
    @Transactional
    public TagRequest createTagApi(TagRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Tag name cannot be blank");
        }

        String normalized = request.name().trim().toLowerCase();
        Tag tag = tagRepository.findByNameLower(normalized)
                .orElseGet(() -> tagRepository.save(Tag.builder()
                        .name(request.name().trim())
                        .nameLower(normalized)
                        .build()));

        return new TagRequest(tag.getId(), tag.getName());
    }

    @Override
    @Transactional
    public void deleteTagApi(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
        tagRepository.delete(tag);
    }
}
