package com.mountblue.blogApplication.dto;


import jakarta.persistence.Lob;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Email
    @NotBlank
    private String email;

    @Lob
    @NotBlank(message = "Comment is reuired")
    private String comment;

    private Long postId;

    private boolean isApproved = true;
}
