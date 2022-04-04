package io.github.miwurster.memento.repository;

import io.github.miwurster.memento.model.ArticleMemento;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMementoRepository extends MementoRepository<ArticleMemento> {

    @Query(value = "SELECT a FROM ArticleMemento a WHERE a.article.entityId = ?1 ORDER BY a.article.revisionNumber ASC")
    List<ArticleMemento> findAllByArticleId(UUID articleId);
}
