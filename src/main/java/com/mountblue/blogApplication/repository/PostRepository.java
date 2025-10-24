package com.mountblue.blogApplication.repository;

import com.mountblue.blogApplication.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.tags")
    List<Post> findAllWithTags();

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.tags WHERE p.id IN :ids")
    List<Post> findAllByIdWithTags(@Param("ids") List<Long> ids);


}
