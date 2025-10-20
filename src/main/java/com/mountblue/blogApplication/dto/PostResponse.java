package com.mountblue.blogApplication.dto;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record PostResponse(
        Long id,
        String title,
        String content,
        String excerpt,
        Long authorId,
        String authorName,
        Boolean isPublished,
        Set<String> tags,
        Instant createdAt,
        Instant updatedAt
) {
}
