package io.github.miwurster.memento.repository;

import io.github.miwurster.memento.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, RevisionRepository<Comment, Long, Integer> {

}
