package com.mountblue.blogApplication.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    @Column(nullable = true, columnDefinition = "TEXT")
    private String excerpt;

    @Lob
    @NotBlank
    @Column(nullable = false)
    private String content;

    @Column(length = 255)
    private String  author;

    private LocalDateTime publishedAt;
    private boolean isPublished = false;


    private Instant createdAt;
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }

//    public void addTag(Tag tag) {
//        this.tags.add(tag);
//        tag.getPosts().add(this);
//    }
//
//    public void removeTag(Tag tag) {
//        this.tags.remove(tag);
//        tag.getPosts().remove(this);
//    }
//
//    public void setTags(Set<Tag> newTags) {
//        for (Tag tag : new HashSet<>(this.tags)) {
//            removeTag(tag);
//        }
//        if (newTags != null) {
//            for (Tag tag : newTags) {
//                addTag(tag);
//            }
//        }
//    }
//
//    public void replaceTags(Set<Tag> newTags) {
//        for (Tag tag : new HashSet<>(this.tags)) {
//            removeTag(tag);
//        }
//        if (newTags != null) {
//            for (Tag tag : newTags) {
//                addTag(tag);
//            }
//        }
//    }

//    public String getOrGenerateExcerpt() {
//        if (excerpt != null && !excerpt.isBlank()) {
//            return excerpt;
//        }+
//        if (content == null) return "";
//        int limit = 150;
//        return content.length() <= limit ? content : content.substring(0, limit) + "...";
//    }

//    public void setPublished(boolean published) {
//        this.isPublished = published;
//        this.publishedAt = published ? LocalDateTime.now() : null;
//    }

//
//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<Comment> comments = new HashSet<>();

}
