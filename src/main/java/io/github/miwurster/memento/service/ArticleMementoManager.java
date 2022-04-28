package io.github.miwurster.memento.service;

import io.github.miwurster.memento.entity.Article;
import io.github.miwurster.memento.entity.ArticleMemento;
import io.github.miwurster.memento.entity.memento.MementoType;
import io.github.miwurster.memento.repository.ArticleMementoRepository;
import io.github.miwurster.memento.repository.ArticleRepository;
import io.github.miwurster.memento.service.support.MementoManager;
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

    @Override
    @Transactional
    public Article revertTo(ArticleMemento memento) {
        return null;
    }

//
//    public Article undo(Article a) {
//        var article = articleRepository.findById(a.getId()).orElseThrow();
//        var mementos = mementoRepository.findAllByArticleId(article.getId());
//        var m = mementos.get(mementos.size() - 2);
//
//        var articleRevision = articleRepository.findRevision(m.getArticle().getEntityId(), m.getArticle().getRevisionNumber()).orElseThrow();
//        var commentRevisions = m.getComments().stream()
//            .map(e -> commentRepository.findRevision(e.getEntityId(), e.getRevisionNumber()).orElseThrow())
//            .collect(Collectors.toList());
//
//        article = articleRepository.findById(m.getArticle().getEntityId()).orElseThrow();
//        // update properties
//        article.setName(articleRevision.getEntity().getName());
//
//        // update article
//        article = articleRepository.save(article);
//
//        var commentsToRestore = commentRevisions.stream().map(Revision::getEntity).collect(Collectors.toList());
//        for (Comment comment : commentsToRestore) {
//            comment.setArticle(article);
//        }
//
//        List<Comment> commentsToSave = new ArrayList<>();
//        List<Comment> commentsToDelete = new ArrayList<>();
//
//        for (Comment comment : article.getComments()) {
//            if (commentsToRestore.contains(comment)) {
//                // replace attributes
//                var i = commentsToRestore.indexOf(comment);
//                var commentToRestore = commentsToRestore.get(i);
//                comment.setName(commentToRestore.getName());
//
//                commentsToSave.add(comment);
//            } else {
//                commentsToDelete.add(comment);
//            }
//        }
//
//        for (Comment comment : commentsToRestore) {
//            if (!article.getComments().contains(comment)) {
//                commentsToSave.add(comment);
//            }
//        }
//
//        commentRepository.deleteAll(commentsToDelete);
//        commentRepository.saveAll(commentsToSave);
//
//        // create memento of fresh article
//        article = articleRepository.findById(article.getId()).orElseThrow();
//        var articleMemento = createMemento(article, MementoType.UPDATE);
//        mementoRepository.save(articleMemento);
//
//        return article;
//    }
}
