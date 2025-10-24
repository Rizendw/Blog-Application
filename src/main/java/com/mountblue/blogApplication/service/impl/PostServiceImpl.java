package com.mountblue.blogApplication.service.impl;

import com.mountblue.blogApplication.dto.PostRequest;
import com.mountblue.blogApplication.dto.PostResponse;
import com.mountblue.blogApplication.entity.Post;
import com.mountblue.blogApplication.entity.Tag;
import com.mountblue.blogApplication.entity.User;
import com.mountblue.blogApplication.repository.PostRepository;
import com.mountblue.blogApplication.repository.TagRepository;
import com.mountblue.blogApplication.service.PostService;
import com.mountblue.blogApplication.service.TagService;
import com.mountblue.blogApplication.service.UserService;
import com.mountblue.blogApplication.specification.PostSpecification;
import org.springframework.security.access.AccessDeniedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl  implements PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final UserService userService;

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public PostResponse createPost(PostRequest postRequest) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null)
            throw new AccessDeniedException("You must be logged in");

        User authorUser = currentUser;
        if (currentUser.getIsAdmin() && postRequest.authorId() != null) {
            authorUser = userService.findById(postRequest.authorId());
        }

//        Set<Tag> tags = postRequest.tagList() == null
//                ? Set.of()
//                : postRequest.tagList().stream()
//                .map(name -> tagService.createTag(new TagRequest(null, name)))
//                .map(tagRequest -> {
//                    Tag tag = new Tag();
//                    tag.setId(tagRequest.id());
//                    tag.setName(tagRequest.name());
//                    tag.setNameLower(tagRequest.name().toLowerCase());
//                    return tag;
//                })
//                .collect(Collectors.toSet());

        Post post = Post.builder()
                .title(postRequest.title())
                .content(postRequest.content())
                .excerpt(generateExcerpt(postRequest.content()))
                .isPublished(Boolean.TRUE.equals(postRequest.isPublished()))
                .author(authorUser.getName())
                .aUser(authorUser)
                .tags(normalizeAndFindTags(postRequest.tagList()))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Post saved = postRepository.save(post);
        return toDto(saved);
    }

    @Override
    public Page<PostResponse> getPaginatedPosts(int page, int size) {
        PageRequest pageable = PageRequest.of(Math.max(page, 0), size);
        Page<Post> posts = postRepository.findAll(pageable);

        List<PostResponse> responses = posts.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, posts.getTotalElements());
    }


    @Override
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
        return toDto(post);
    }

    @Override
    @Transactional
    public PostResponse updatePost(Long id, PostRequest postRequest) {
        System.err.println("i'm here  in update");
        User currentUser = userService.getCurrentUser();
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean isAuthor = post.getAUser().getId().equals(currentUser.getId());
        if (!currentUser.getIsAdmin() && !isAuthor) {
            throw new AccessDeniedException("You cannot edit this post");
        }

        post.setTitle(postRequest.title());
        post.setContent(postRequest.content());
        post.setExcerpt(generateExcerpt(postRequest.content()));
        post.setTags(normalizeAndFindTags(postRequest.tagList()));
        post.setIsPublished(Boolean.TRUE.equals(postRequest.isPublished()));
        post.setUpdatedAt(Instant.now());

        Post updated = postRepository.save(post);
        return toDto(updated);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        User currentUser = userService.getCurrentUser();
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean isAuthor = post.getAUser().getId().equals(currentUser.getId());
        if (!currentUser.getIsAdmin() && !isAuthor) {
            throw new AccessDeniedException("You cannot delete this post");
        }
        postRepository.delete(post);
    }

    @Override
    public Page<PostResponse> searchPosts(String search, List<Long> tagId, String author,
                                          Boolean isPublished, Instant from, Instant to,
                                          int page, int size, String sortField, String sortDir) {
        Specification<Post> specification = Specification.allOf(
                PostSpecification.containsText(search),
                PostSpecification.hasAuthor(author),
                PostSpecification.isPublished(isPublished),
                PostSpecification.hasAnyTag(tagId),
                PostSpecification.createdBetween(from, to)
        );

        String sortProperty = (sortField == null || sortField.isEmpty()) ? "updatedAt" : sortField;
        Sort.Direction direction = ("asc".equalsIgnoreCase(sortDir)) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(1, size), Sort.by(direction, sortProperty));

        Page<Post> posts = postRepository.findAll(specification, pageable);
        List<Long> postIds = posts.stream().map(Post::getId).toList();


        List<Post> postsWithTags = postIds.isEmpty()
                ? List.of()
                : postRepository.findAllByIdWithTags(postIds);

            // Map postsWithTags to DTOs
        List<PostResponse> responses = postsWithTags.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, posts.getTotalElements());
    }

    private PostResponse toDto(Post saved) {

        Set<String> tagNames = saved.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        return new PostResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getExcerpt(),
                saved.getAUser().getId(),
                saved.getAuthor(),
                saved.getIsPublished(),
                tagNames,
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    private Set<Tag> normalizeAndFindTags(Set<String> rawTags) {
        if (rawTags == null || rawTags.isEmpty()) return new LinkedHashSet<>();
        Set<Tag> tags = new LinkedHashSet<>();
        for (String raw : rawTags) {
            if (raw == null || raw.isBlank()) continue;
            String normalized = raw.trim().toLowerCase();
            Tag tag = tagRepository.findByNameLower(normalized)
                    .orElseGet(() -> tagRepository.save(Tag.builder()
                            .name(raw.trim())
                            .nameLower(normalized)
                            .build()));
            tags.add(tag);
        }
        return tags;
    }

    private String stripHtml(String text) {
        if (text == null) return "";
        return text.replaceAll("<[^>]*>", " ");
    }

    private String generateExcerpt(String content) {
        if (content == null || content.isBlank()) return "";

        String[] words = content.trim().split("\\s+");
        if (words.length <= 100) {
            return content.trim();
        }

        String firstPart = String.join(" ", Arrays.copyOfRange(words, 0, 250));
        int endIndex = findSentenceEnd(content, firstPart.length());
        String excerpt = content.substring(0, endIndex).trim();
        return excerpt;
    }

    private int findSentenceEnd(String text, int startIndex) {
        int len = text.length();
        for (int i = startIndex; i < len; i++) {
            char c = text.charAt(i);
            if (c == '.' || c == '?' || c == '!') {
                return i + 1;
            }
        }
        return len;
    }

    @Override
    public Post getPostEntity(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
    }

}