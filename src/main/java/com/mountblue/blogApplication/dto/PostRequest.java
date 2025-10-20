package com.mountblue.blogApplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record PostRequest(
        Long id,
        Long authorId,
        @NotBlank @Size(max = 255) String title,
        @NotBlank String content,
        Boolean isPublished,
        Set<String> tagList
) {
}
