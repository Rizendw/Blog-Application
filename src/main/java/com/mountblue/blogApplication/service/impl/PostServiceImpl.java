package com.mountblue.blogApplication.service.impl;

import com.mountblue.blogApplication.dto.CreatePostRequest;
import com.mountblue.blogApplication.dto.PostResponse;
import com.mountblue.blogApplication.entity.Post;
import com.mountblue.blogApplication.entity.Tag;
import com.mountblue.blogApplication.repository.PostRepository;
import com.mountblue.blogApplication.repository.TagRepository;
import com.mountblue.blogApplication.service.PostService;
import com.mountblue.blogApplication.specification.PostSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl  implements PostService {

    private  final PostRepository postRepository;
    private  final TagRepository tagRepository;

    @Override
    @Transactional
    public PostResponse createPost(CreatePostRequest request) {
        Set<Tag> tagEntities = new LinkedHashSet<>();

        if (request.tagList() != null) {
            for (String rawTag : request.tagList()) {
                if (rawTag == null || rawTag.isBlank()) continue;
                String normalized = rawTag.trim().toLowerCase();
                Tag tag = tagRepository.findByNameLower(normalized)
                        .orElseGet(() -> tagRepository.save(Tag.builder()
                                .name(rawTag.trim())
                                .nameLower(normalized)
                                .build()));
                tagEntities.add(tag);
            }
        }

        String cleanContent = stripHtml(request.content());
        String excerpt = generateExcerpt(cleanContent);

        Post post = Post.builder()
                .title(request.title().trim())
                .content(cleanContent.trim())
                .excerpt(excerpt)
                .author(request.author().trim())
                .tags(tagEntities)
                .build();

        Post saved = postRepository.save(post);

        return new PostResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getExcerpt(),
                saved.getAuthor(),
                saved.getCreatedAt(),
                saved.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList())
        );
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
    public PostResponse updatePost(Long id, CreatePostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));

        post.setTitle(request.title().trim());
        String cleanContent = stripHtml(request.content());
        post.setContent(cleanContent);
        post.setExcerpt(generateExcerpt(cleanContent));
        post.setAuthor(request.author().trim());

        Set<Tag> updatedTags = normalizeAndFindTags(request.tagList());
        post.getTags().clear();
        post.getTags().addAll(updatedTags);

        Post updated = postRepository.save(post);
        return toDto(updated);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
        postRepository.delete(post);
    }

    @Override
    public Page<PostResponse> searchPosts(String search, List<Long> tagId, String author, Boolean isPublished, Instant from, Instant to, int page, int size, String sortField, String sortDir) {
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

        Page<Post> posts = postRepository.findAll(specification,pageable);

        List<PostResponse> responses = posts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, posts.getTotalElements());
    }

        //  HELPER METHODS
    private PostResponse toDto(Post saved) {
        return new PostResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getExcerpt(),
                saved.getAuthor(),
                saved.getCreatedAt(),
                saved.getTags().stream().map(Tag::getName).toList()
        );
    }

    private Set<Tag> normalizeAndFindTags(List<String> rawTags) {
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

    /** Utility to strip simple HTML tags (best-effort). */
    private String stripHtml(String text) {
        if (text == null) return "";
        return text.replaceAll("<[^>]*>", " ");
    }

    /**
     * Generate excerpt: first 250 words, extend to next sentence terminator (.?!)
     */
    private String generateExcerpt(String content) {
        if (content == null || content.isBlank()) return "";

        String[] words = content.trim().split("\\s+");
        if (words.length <= 120) {
            return content.trim();
        }

        // Join first 250 words
        String firstPart = String.join(" ", Arrays.copyOfRange(words, 0, 250));
        int endIndex = findSentenceEnd(content, firstPart.length());
        String excerpt = content.substring(0, endIndex).trim();
        return excerpt;
    }

    /** Find the nearest sentence end ('.', '?', '!') after the given index */
    private int findSentenceEnd(String text, int startIndex) {
        int len = text.length();
        for (int i = startIndex; i < len; i++) {
            char c = text.charAt(i);
            if (c == '.' || c == '?' || c == '!') {
                return i + 1;
            }
        }
        // no further sentence terminator — return whole text
        return len;
    }

//    @Override
//    public PostDto createPost(PostDto dto) {
//        Post post = new Post();
//
//        mapDtoToEntity(dto, post, true);
//        if(post.isPublished() && post.getPublishedAt() == null) {
//            post.setPublished(true);
//            post.setPublishedAt(LocalDateTime.now());
//        }
//        Post savedPost = postRepository.save(post);
//        return mapEntityToDto(savedPost);
//    }
//
//    @Override
//    public PostDto updatePost (Long id, PostDto dto) {
//        Post post = postRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Post not found"));
//        mapDtoToEntity(dto, post, false);
//        if(post.isPublished() && post.getPublishedAt() == null) {
//            post.setPublished(true);
//            post.setPublishedAt(LocalDateTime.now());
//        }
//        Post savedPost = postRepository.save(post);
//        return mapEntityToDto(savedPost);
//    }
//
//    @Override
//    public Optional<PostDto> getPostById(Long id) {
//        return postRepository.findById(id).map(this::mapEntityToDto);
//    }
//
//
//    @Override
//    public Page<PostDto> searchPosts(String search, Long authorId, List<Long> tagIds, String tagMode, String sortField, String order, Pageable pageable) {
//
//        Specification<Post> specification = Specification.allOf();
//
//        if(StringUtils.hasText(search)){
//            specification = specification.and(PostSpecification.containsTextInFeilds(search));
//        }
//        if(authorId != null){
//            specification = specification.and(PostSpecification.hasAuthor(authorId));
//        }
//
//        boolean andMode = "AND".equalsIgnoreCase(tagMode);
//
//        if(tagIds != null && !tagIds.isEmpty()){
//            if(andMode){
//                specification = specification.and(PostSpecification.hasAnyTag(tagIds));
//                Page<Post> page = postRepository.findAll(specification, buildSortAntPageable(sortField, order, pageable));
//                return page.map(this::mapEntityToDto);
//            } else {
//                return  searchPostsWithTagAnd(tagIds, specification, sortField, order, pageable);
//            }
//        }
//        else {
//            Page<Post> page = postRepository.findAll(specification, buildSortAntPageable(sortField, order, pageable));
//            return page.map(this::mapEntityToDto);
//        }
//    }
//
//    @Override
//    public List<PostDto> listAllPosts() {
//        return postRepository.findAll()
//                .stream()
//                .map(this::mapEntityToDto)
//                .collect(Collectors.toList());
//    }
//
//    private Page<PostDto> searchPostsWithTagAnd(List<Long> tagIds, Specification<Post> specification, String sortField, String order, Pageable pageable) {
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);
//        Root<Post> root = criteriaQuery.from(Post.class);
//        root.fetch("author", JoinType.LEFT);
//        Join<Object, Object> joinTags = root.join("tags");
//        List<Long> ids = tagIds;
//
//        Predicate basePredicate = null;
//        if (specification  != null) {
//            basePredicate = specification .toPredicate(root, criteriaQuery, criteriaBuilder);
//        }
//
//        Predicate inTags = joinTags.get("id").in(ids);
//
//        Predicate finalPredicate = (basePredicate != null)
//                ? criteriaBuilder.and(basePredicate, inTags)
//                : inTags;
//        criteriaQuery.where(finalPredicate);
//
//        criteriaQuery.groupBy(root.get("id"));
//
//        criteriaQuery.having(criteriaBuilder.equal(criteriaBuilder.countDistinct(joinTags.get("id")), ids.size()));
//
//        if("asc".equalsIgnoreCase(order)){
//            criteriaQuery.orderBy(criteriaBuilder.asc(
//                    root.get(sortField == null ? "publishedAt" : sortField)));
//        } else {
//            criteriaQuery.orderBy(criteriaBuilder.desc(
//                    root.get(sortField == null ? "publishedAt" : sortField)));
//        }
//
//        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
//        Root<Post> countRoot = countQuery.from(Post.class);
//        countQuery.select(criteriaBuilder.count(countRoot));
//        Join<Object, Object> countJoin = countRoot.join("tags");
//        Predicate count = countRoot.get("id").in(
//                entityManager.createQuery(criteriaBuilder.createQuery(Post.class)).getResultList()
//        );
//
//        List<Post> resultList = entityManager.createQuery(criteriaQuery).getResultList();
//
//        int pageSize = pageable.getPageSize();
//        int currentPage = pageable.getPageNumber();
//        int startItem = currentPage * pageSize;
//        List<PostDto> dtos;
//        if (resultList.size() < startItem) {
//            dtos = Collections.emptyList();
//        } else {
//            int toIndex = Math.min(startItem + pageSize, resultList.size());
//            dtos = resultList.subList(startItem, toIndex).stream()
//                    .map(this::mapEntityToDto).collect(Collectors.toList());
//        }
//        return new PageImpl<>(dtos, pageable, resultList.size());
//    }
//
//    private Pageable buildSortAntPageable(String sortField, String order, Pageable pageable) {
//        Sort sort;
//        if(sortField == null){
//            sortField = "publishedAt";
//        }
//        if("asc".equalsIgnoreCase(order)){
//            sort = Sort.by(sortField).ascending();
//        }
//        else{
//            sort = Sort.by(sortField).descending();
//        }
//        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
//    }
//
//    private PostDto mapEntityToDto(Post savedPost) {
//        PostDto dto = PostDto.builder()
//                .id(savedPost.getId())
//                .title(savedPost.getTitle())
//                .content(savedPost.getContent())
//                .isPublished(savedPost.isPublished())
//                .publishedAt(savedPost.getPublishedAt())
//                .build();
//
//        dto.setTagList(savedPost.getTags().stream()
//                .map(Tag::getName)
//                .collect(Collectors.toList()));
//        dto.setTags(String.join(",", dto.getTagList()));
//        return dto;
//    }
//
//    private void mapDtoToEntity(PostDto dto, Post post, boolean createNew) {
//        post.setTitle(dto.getTitle());
//        post.setContent(dto.getContent());
//        post.setPublished(dto.isPublished());
//        if(dto.getPublishedAt() != null){
//            post.setPublishedAt(dto.getPublishedAt());
//        }
//
//        Set<Tag> parsedTags = new HashSet<>();
//        if(dto.getTagList() != null && !dto.getTagList().isEmpty()){
//            parsedTags = (Set<Tag>) dto.getTagList().stream()
//                    .filter(Objects::nonNull)
//                    .map(String::trim)
//                    .filter(s -> !s.isBlank())
//                    .map(this::getOrCreateTagByName)
//                    .collect(Collectors.toSet());
//        }
//
//        post.replaceTags(parsedTags);
//    }
//
//    private Tag getOrCreateTagByName(String name){
//        return tagRepository.findByNameLower(name)
//                .orElseGet(() ->
//                        tagRepository.save(Tag.builder().name(name).build()));
//    }
//
//
///**
// * Parse user-provided tag string and ensure tags exist in DB (unique by name, case-insensitive).
// * Returns a Set<Tag> ready to attach to Post.
// *
// * Tag normalization policy:
// *  - split on commas and whitespace,
// *  - trim each token,
// *  - ignore empty tokens,
// *  - store tag name in the database as-is but uniqueness is case-insensitive.
// */
//
//private Set<Tag> parseAndPersistTags(String tagInput) {
//    if(!StringUtils.hasText((tagInput))){
//        return new LinkedHashSet<>();
//    }
//
//    // split by comma or whitespace; keep token order but de-duplicate via LinkedHashSet
//    String[] raw = tagInput.trim().split("[,;]+|\\s+");
//    Set<String> tagNames = Stream.of(raw)
//            .map(String::trim)
//            .filter(s -> !s.isEmpty())
//            .collect(Collectors.toCollection(LinkedHashSet::new));
//
//    Set<Tag> tags = new LinkedHashSet<>();
//    for (String tag : tagNames){
//        tagRepository.findByNameIgnoreCase(tag)
//                .ifPresentOrElse(
//                        tags::add,
//                        () -> {
//                            Tag newTag = new Tag();
//                            newTag.setName(tag);
//                            Tag savedTag = tagRepository.save(newTag);
//                            tags.add(savedTag);
//                        }
//                );
//    }
//    return tags;
//}
//
//private String generateExcerpt(String content, int maxLength) {
//    if(!StringUtils.hasText(content)) return "";
//    String s = content.trim();
//    if(s.length() <= maxLength) return s;
//    int lastPeriod = s.lastIndexOf('.', maxLength);
//    if(lastPeriod > 0 && lastPeriod > maxLength / 2){
//        return s.substring(0, lastPeriod + 1);
//    }
//    return s.substring(0, maxLength) + "...";
//}
}
