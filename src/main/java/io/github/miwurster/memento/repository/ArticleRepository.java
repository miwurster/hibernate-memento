package io.github.miwurster.memento.repository;

import io.github.miwurster.memento.entity.Article;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID>, RevisionRepository<Article, UUID, Integer> {

}
