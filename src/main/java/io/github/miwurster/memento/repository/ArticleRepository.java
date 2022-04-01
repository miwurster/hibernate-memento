package io.github.miwurster.memento.repository;

import io.github.miwurster.memento.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, RevisionRepository<Article, Long, Integer> {

}
