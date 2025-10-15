package com.mountblue.blogApplication.dto;

import java.time.Instant;
import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String content,
        String excerpt,
        String author,
        Instant createdAt,
        List<String> tags
) {
}
