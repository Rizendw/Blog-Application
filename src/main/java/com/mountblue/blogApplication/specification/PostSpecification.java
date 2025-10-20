package com.mountblue.blogApplication.specification;

import com.mountblue.blogApplication.entity.Post;
import com.mountblue.blogApplication.entity.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class PostSpecification {

    public static Specification<Post> containsText(String text) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
           if(text == null || text.isBlank())
               return null;

           String pattern = "%" + text.trim().toLowerCase() + "%";

           Join<Post, Tag> tags = root.join("tags", JoinType.LEFT);
           criteriaQuery.distinct(true);

           return criteriaBuilder.or(
               criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
               criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), pattern),
               criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), pattern),
               criteriaBuilder.like(criteriaBuilder.lower(tags.get("nameLower")), pattern)
           );

        });
    }

    public static Specification<Post> hasAuthor(String author) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (author == null || author.isBlank())
                return null;

            String pattern = "%" + author.trim().toLowerCase() + "%";
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), pattern);
        };
    }

    public static Specification<Post> isPublished(Boolean isPublished){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(isPublished == null)
                return null;

            return criteriaBuilder.equal(root.get("isPublished"), isPublished);
        };
    }

        public static Specification<Post> createdBetween(Instant from, Instant to) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(from == null || to == null)
                return null;

            if(to == null){
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), from);
            }
            else if (from == null){
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), to);
            }
            else {
                return criteriaBuilder.between(root.get("createdAt"), from, to);
            }
        };
    }

    public static Specification<Post> hasAnyTag(List<Long> tagIds){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(tagIds == null || tagIds.isEmpty())
                return null;

            Join<Post, Tag> tags =  root.join("tags", JoinType.INNER); //root is the Post entity
            criteriaQuery.distinct(true);
            return tags.get("id").in(tagIds);
        };
    }
 }

