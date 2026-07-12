package com.mombaby.radar.repository;

import com.mombaby.radar.entity.Comment;
import com.mombaby.radar.entity.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByStatus(CommentStatus status, Pageable pageable);

    List<Comment> findByStatus(CommentStatus status);

    long countByStatus(CommentStatus status);
}
