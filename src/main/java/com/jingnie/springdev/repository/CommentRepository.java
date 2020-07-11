package com.jingnie.springdev.repository;

import com.jingnie.springdev.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {

}
