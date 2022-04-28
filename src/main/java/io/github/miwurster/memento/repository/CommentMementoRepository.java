package io.github.miwurster.memento.repository;

import io.github.miwurster.memento.model.CommentMemento;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentMementoRepository extends MementoRepository<CommentMemento> {

    @Query(value = "SELECT a FROM CommentMemento a WHERE a.comment.entityId = ?1 ORDER BY a.comment.revisionNumber ASC")
    List<CommentMemento> findAllByCommentId(UUID commentId);
}
