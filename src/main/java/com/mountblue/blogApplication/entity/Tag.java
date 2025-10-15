package com.mountblue.blogApplication.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "tags",
        uniqueConstraints = @UniqueConstraint(columnNames = {"normalized_name"}))
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String nameLower;

    private Instant createdAt;

    private Instant updatedAt; @Column(nullable = false, unique = true)

    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new HashSet<>();

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }


//    public void addPost(Post post) {
//        posts.add(post);
//        post.getTags().add(this);
//    }
//
//    public void removePost(Post post) {
//        posts.remove(post);
//        post.getTags().remove(this);
//    }
}
