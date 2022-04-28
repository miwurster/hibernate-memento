package io.github.miwurster.memento.repository;

import io.github.miwurster.memento.entity.ArticleMemento;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMementoRepository extends MementoRepository<ArticleMemento> {

    @Query(value = "SELECT a FROM ArticleMemento a WHERE a.entityId = ?1 ORDER BY a.modifiedAt ASC")
    List<ArticleMemento> findAllByArticleId(UUID articleId);
}
