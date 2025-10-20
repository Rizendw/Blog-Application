package com.mountblue.blogApplication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CommentRequest(
        @NotBlank String name,
        @Email String email,
        @NotBlank String comment)
{
}
