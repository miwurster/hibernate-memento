package io.github.miwurster.memento.service;

import io.github.miwurster.memento.entity.Article;
import io.github.miwurster.memento.entity.Comment;
import io.github.miwurster.memento.entity.PersistentObject;
import io.github.miwurster.memento.model.ArticleMemento;
import io.github.miwurster.memento.model.EntityRevision;
import io.github.miwurster.memento.model.Memento;
import io.github.miwurster.memento.model.MementoType;
import io.github.miwurster.memento.repository.ArticleRepository;
import io.github.miwurster.memento.repository.CommentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleManager {

    private final ArticleRepository articleRepository;

    private final CommentRepository commentRepository;

    // TODO: This needs to be refactored into valid Entity objects to be stored in the database
    @Getter
    private final List<Memento> mementoRepository = new ArrayList<>();

    public Article saveArticle(Article a) {
        // save object
        var article = articleRepository.save(a);
        // create memento
        var articleMemento = createMemento(article, MementoType.INSERT);
        mementoRepository.add(articleMemento);
        // return entity
        return article;
    }

    public Article updateArticle(Article a) {
        // get current state
        var article = articleRepository.findById(a.getId()).orElseThrow();
        // update fields
        article.setName(a.getName());
        article = articleRepository.save(article);
        // create memento
        var articleMemento = createMemento(article, MementoType.UPDATE);
        mementoRepository.add(articleMemento);
        // return entity
        return article;
    }

    public Article deleteArticle(Article a) {
        // get current state
        var article = articleRepository.findById(a.getId()).orElseThrow();
        // delete comments
        commentRepository.deleteAll(article.getComments());
        // delete article
        articleRepository.delete(article);
        // create memento
        var articleMemento = createMemento(article, MementoType.UPDATE);
        mementoRepository.add(articleMemento);
        // return entity
        return article;
    }

    public Article addComment(Article a, Comment c) {
        // get current state
        var article = articleRepository.findById(a.getId()).orElseThrow();
        // add article
        c.setArticle(article);
        commentRepository.save(c);
        // create memento with fresh state
        article = articleRepository.findById(a.getId()).orElseThrow();
        var articleMemento = createMemento(article, MementoType.UPDATE);
        mementoRepository.add(articleMemento);
        // return entity
        return article;
    }

    public Article removeComment(Article a, Comment c) {
        // get current state
        var comment = commentRepository.findById(c.getId()).orElseThrow();
        // remove comment
        commentRepository.delete(comment);
        // create memento with fresh state
        var article = articleRepository.findById(a.getId()).orElseThrow();
        var articleMemento = createMemento(article, MementoType.UPDATE);
        mementoRepository.add(articleMemento);
        // return entity
        return article;
    }

    private ArticleMemento createMemento(Article article, MementoType type) {
        // collect revisions for
        var articleRev = articleRepository.findLastChangeRevision(article.getId()).orElseThrow();
        var commentsRev = article.getComments().stream()
            .map(c -> commentRepository.findLastChangeRevision(c.getId()).orElseThrow())
            .collect(Collectors.toList());
        // create memento
        return ArticleMemento.builder()
            .type(type)
            .article(createEntityRevision(articleRev))
            .comments(commentsRev.stream().map(this::createEntityRevision).collect(Collectors.toList()))
            .build();
    }

    private EntityRevision createEntityRevision(Revision<Integer, ? extends PersistentObject> revision) {
        return EntityRevision.builder()
            .entityId(revision.getEntity().getId())
            .revisionNumber(revision.getRequiredRevisionNumber())
            .build();
    }
}
