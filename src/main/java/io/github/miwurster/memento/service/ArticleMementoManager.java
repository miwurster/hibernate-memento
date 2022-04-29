package io.github.miwurster.memento.service;

import io.github.miwurster.memento.entity.Article;
import io.github.miwurster.memento.entity.ArticleMemento;
import io.github.miwurster.memento.entity.Comment;
import io.github.miwurster.memento.entity.memento.CommentRevision;
import io.github.miwurster.memento.entity.memento.MementoType;
import io.github.miwurster.memento.entity.support.PersistentObject;
import io.github.miwurster.memento.repository.ArticleMementoRepository;
import io.github.miwurster.memento.repository.ArticleRepository;
import io.github.miwurster.memento.service.support.MementoManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleMementoManager implements MementoManager<ArticleMemento, Article> {

    private final ArticleRepository articleRepository;

    private final ArticleMementoRepository articleMementoRepository;

    private final CommentRevisionManager commentRevisionManager;

    private final CommentManager commentManager;

    @Override
    public List<ArticleMemento> getMementos(Article article) {
        return articleMementoRepository.findAllByArticleId(article.getId());
    }

    @Override
    @Transactional
    public ArticleMemento createMemento(Article article, MementoType type) {
        var articleRev = articleRepository.findLastChangeRevision(article.getId()).orElseThrow();
        var commentRevisions = article.getComments().stream()
            .map(commentRevisionManager::createRevision)
            .collect(Collectors.toList());
        ArticleMemento memento = new ArticleMemento();
        memento.setType(type);
        memento.setEntityId(articleRev.getEntity().getId());
        memento.setRevisionNumber(articleRev.getRequiredRevisionNumber());
        memento.setValue(new ArticleMemento.Value(commentRevisions));
        return articleMementoRepository.save(memento);
    }

//        var article = articleRepository.findById(a.getId()).orElseThrow();
//        var mementos = mementoRepository.findAllByArticleId(article.getId());
//        var m = mementos.get(mementos.size() - 2);

    @Override
    @Transactional
    public Article revertTo(ArticleMemento memento) {

        // get revision
        var articleRevision = articleRepository.findRevision(memento.getEntityId(), memento.getRevisionNumber()).orElseThrow();
        // get current version
        var article = articleRepository.findById(memento.getEntityId()).orElseThrow();
        // update properties
        article.setName(articleRevision.getEntity().getName());
        // save entity
        article = articleRepository.save(article);

        // revert comments
        List<Comment> commentsToDelete = new ArrayList<>();
        var commentRevisions = memento.getValue().getCommentRevisions();
        for (Comment comment : article.getComments()) {
            var filter = commentRevisions.stream().filter(r -> comment.getId().equals(r.getEntityId()));
            if (filter.count() == 1) {
                var revision = filter.findFirst().orElseThrow();
                commentRevisionManager.revertTo(revision);
            } else {
                commentsToDelete.add(comment);
            }
        }

        var commentIds = article.getComments().stream().map(PersistentObject::getId).collect(Collectors.toList());
        for (CommentRevision revision : commentRevisions) {
            if (!commentIds.contains(revision.getEntityId())) {
                commentRevisionManager.revertTo(revision);
            }
        }

        for (Comment comment : commentsToDelete) {
            commentManager.deleteComment(comment);
        }

        return articleRepository.findById(article.getId()).orElseThrow();
    }
}
