package io.github.miwurster.memento.service;

import io.github.miwurster.memento.entity.Article;
import io.github.miwurster.memento.entity.Comment;
import io.github.miwurster.memento.entity.PersistentObject;
import io.github.miwurster.memento.model.ArticleMemento;
import io.github.miwurster.memento.model.EntityRevision;
import io.github.miwurster.memento.model.MementoType;
import io.github.miwurster.memento.repository.ArticleMementoRepository;
import io.github.miwurster.memento.repository.ArticleRepository;
import io.github.miwurster.memento.repository.CommentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleManager {

    private final ArticleRepository articleRepository;

    private final CommentRepository commentRepository;

    private final ArticleMementoRepository mementoRepository;

    public Article createArticle(Article a) {
        // save object
        var article = articleRepository.save(a);
        // create memento
        var articleMemento = createMemento(article, MementoType.CREATE);
        mementoRepository.save(articleMemento);
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
        mementoRepository.save(articleMemento);
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
        var articleMemento = createMemento(article, MementoType.DELETE);
        mementoRepository.save(articleMemento);
        // return entity
        return article;
    }

    public Article createComment(Article a, Comment c) {
        // get current state
        var article = articleRepository.findById(a.getId()).orElseThrow();
        // add article
        c.setArticle(article);
        commentRepository.save(c);
        // create memento with fresh state
        article = articleRepository.findById(a.getId()).orElseThrow();
        var articleMemento = createMemento(article, MementoType.UPDATE);
        mementoRepository.save(articleMemento);
        // return entity
        return article;
    }

    public Article updateComment(Article a, Comment c) {
        // get current state
        var article = articleRepository.findById(a.getId()).orElseThrow();
        var comment = commentRepository.findById(c.getId()).orElseThrow();
        // delete comment if it belongs to the given article
        if (article.getComments().contains(comment)) {
            comment.setName(c.getName());
            commentRepository.save(comment);
        }
        // create memento with fresh state
        article = articleRepository.findById(a.getId()).orElseThrow();
        var articleMemento = createMemento(article, MementoType.UPDATE);
        mementoRepository.save(articleMemento);
        // return entity
        return article;
    }

    public Article deleteComment(Article a, Comment c) {
        // get current state
        var comment = commentRepository.findById(c.getId()).orElseThrow();
        // remove comment
        commentRepository.delete(comment);
        // create memento with fresh state
        var article = articleRepository.findById(a.getId()).orElseThrow();
        var articleMemento = createMemento(article, MementoType.UPDATE);
        mementoRepository.save(articleMemento);
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
        ArticleMemento memento = new ArticleMemento();
        memento.setType(type);
        memento.setArticle(createEntityRevision(articleRev));
        memento.setComments(commentsRev.stream().map(this::createEntityRevision).collect(Collectors.toList()));
        return memento;
    }

    private EntityRevision createEntityRevision(Revision<Integer, ? extends PersistentObject> revision) {
        EntityRevision rev = new EntityRevision();
        rev.setEntityId(revision.getEntity().getId());
        rev.setRevisionNumber(revision.getRequiredRevisionNumber());
        return rev;
    }

    public Article undo(Article a) {
        var article = articleRepository.findById(a.getId()).orElseThrow();
        var mementos = mementoRepository.findAllByArticleId(article.getId());
        var m = mementos.get(mementos.size() - 2);

        var articleRevision = articleRepository.findRevision(m.getArticle().getEntityId(), m.getArticle().getRevisionNumber()).orElseThrow();
        var commentRevisions = m.getComments().stream()
            .map(e -> commentRepository.findRevision(e.getEntityId(), e.getRevisionNumber()).orElseThrow())
            .collect(Collectors.toList());

        article = articleRepository.findById(m.getArticle().getEntityId()).orElseThrow();
        // update properties
        article.setName(articleRevision.getEntity().getName());

        // update article
        article = articleRepository.save(article);

        var commentsToRestore = commentRevisions.stream().map(Revision::getEntity).collect(Collectors.toList());
        for (Comment comment : commentsToRestore) {
            comment.setArticle(article);
        }

        List<Comment> commentsToSave = new ArrayList<>();
        List<Comment> commentsToDelete = new ArrayList<>();

        for (Comment comment : article.getComments()) {
            if (commentsToRestore.contains(comment)) {
                // replace attributes
                var i = commentsToRestore.indexOf(comment);
                var commentToRestore = commentsToRestore.get(i);
                comment.setName(commentToRestore.getName());

                commentsToSave.add(comment);
            } else {
                commentsToDelete.add(comment);
            }
        }

        for (Comment comment : commentsToRestore) {
            if (!article.getComments().contains(comment)) {
                commentsToSave.add(comment);
            }
        }

        commentRepository.deleteAll(commentsToDelete);
        commentRepository.saveAll(commentsToSave);

        // create memento of fresh article
        article = articleRepository.findById(article.getId()).orElseThrow();
        var articleMemento = createMemento(article, MementoType.UPDATE);
        mementoRepository.save(articleMemento);

        return article;
    }
}
