package com.mountblue.blogApplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreatePostRequest(
        @NotBlank @Size(max = 255) String title,
        @NotBlank String content,
        String author,
        List<@NotBlank String> tagList
) {
}
