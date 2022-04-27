package io.github.miwurster.memento.service;

import io.github.miwurster.memento.entity.Article;
import io.github.miwurster.memento.repository.ArticleRepository;
import io.github.miwurster.memento.repository.CommentRepository;
import io.github.miwurster.memento.repository.FileRepository;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleManager {

    private final ArticleRepository articleRepository;

    private final CommentRepository commentRepository;

    private final FileRepository fileRepository;

    public Article findById(UUID id) {
        return articleRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Article createArticle(Article article) {
        return articleRepository.save(article);
    }

    @Transactional
    public Article updateArticle(Article article) {
        var persistedArticle = articleRepository.findById(article.getId()).orElseThrow();
        persistedArticle.setName(article.getName());
        return articleRepository.save(persistedArticle);
    }

    @Transactional
    public void deleteArticle(Article article) {
        var persistedArticle = articleRepository.findById(article.getId()).orElseThrow();
        fileRepository.deleteAll(persistedArticle.getComments().stream().flatMap(c -> c.getFiles().stream()).collect(Collectors.toList()));
        commentRepository.deleteAll(persistedArticle.getComments());
        articleRepository.delete(persistedArticle);
    }
}
