package com.mountblue.blogApplication.dto;

import java.time.Instant;
import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String content,
        String excerpt,
        Long authorId,
        String authorName,
        boolean isPublished,
        List<String> tags,
        Instant createdAt,
        Instant updatedAt
) {
}
