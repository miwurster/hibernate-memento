package io.github.miwurster.memento.service;

import io.github.miwurster.memento.entity.Article;
import io.github.miwurster.memento.model.ArticleMemento;
import io.github.miwurster.memento.model.MementoType;
import io.github.miwurster.memento.repository.ArticleMementoRepository;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleMementoManager implements MementoManager<ArticleMemento, Article> {

    private final ArticleMementoRepository articleMementoRepository;

    @Override
    public List<ArticleMemento> getMementos(Article article) {
        return articleMementoRepository.findAllByArticleId(article.getId());
    }

    @Override
    @Transactional
    public ArticleMemento createMemento(Article article, MementoType type) {
        return null;
    }

    @Override
    @Transactional
    public Article revertTo(ArticleMemento memento) {
        return null;
    }



//    private ArticleMemento createMemento(Article article, MementoType type) {
//        // collect revisions for
//        var articleRev = articleRepository.findLastChangeRevision(article.getId()).orElseThrow();
//        var commentsRev = article.getComments().stream()
//            .map(c -> commentRepository.findLastChangeRevision(c.getId()).orElseThrow())
//            .collect(Collectors.toList());
//        // create memento
//        ArticleMemento memento = new ArticleMemento();
//        memento.setType(type);
//        memento.setArticle(createEntityRevision(articleRev));
//        memento.setComments(commentsRev.stream().map(this::createEntityRevision).collect(Collectors.toList()));
//        return memento;
//    }
//
//    private EntityRevision createEntityRevision(Revision<Integer, ? extends PersistentObject> revision) {
//        EntityRevision rev = new EntityRevision();
//        rev.setEntityId(revision.getEntity().getId());
//        rev.setRevisionNumber(revision.getRequiredRevisionNumber());
//        return rev;
//    }
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
