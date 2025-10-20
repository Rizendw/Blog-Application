package com.mountblue.blogApplication.service.impl;

import com.mountblue.blogApplication.dto.TagRequest;
import com.mountblue.blogApplication.entity.Tag;
import com.mountblue.blogApplication.repository.TagRepository;
import com.mountblue.blogApplication.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

    private TagRequest map(Tag tag) {
        return new TagRequest(tag.getId(), tag.getName());
    }
}
