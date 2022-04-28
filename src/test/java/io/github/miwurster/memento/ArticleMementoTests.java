package io.github.miwurster.memento;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.miwurster.memento.entity.Article;
import io.github.miwurster.memento.entity.Comment;
import io.github.miwurster.memento.entity.File;
import io.github.miwurster.memento.entity.memento.MementoType;
import io.github.miwurster.memento.service.ArticleManager;
import io.github.miwurster.memento.service.ArticleMementoManager;
import io.github.miwurster.memento.service.CommentManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ArticleMementoTests {

    @Autowired
    private ArticleManager articleManager;

    @Autowired
    private CommentManager commentManager;

    @Autowired
    private ArticleMementoManager articleMementoManager;

    @Test
    void basicTests() {
        // add article
        var article = new Article();
        article.setName("Article 1");
        article = articleManager.createArticle(article);
        // add comment
        var comment = new Comment();
        comment.setName("Comment 1");
        comment.setArticle(article);
        comment = commentManager.createComment(article, comment);
        // add file
        var file = new File();
        file.setName("File 1");
        commentManager.addFile(comment, file);

        article = articleManager.findById(article.getId());

        assertThat(article.getComments()).hasSize(1);
        assertThat(article.getComments().get(0).getFiles()).hasSize(1);

        articleManager.deleteArticle(article);
    }

    @Test
    void testShouldCreateMementos() {

        // add article
        var article = new Article();
        article.setName("Article 1");
        article = articleManager.createArticle(article);

        article = articleManager.findById(article.getId());
        articleMementoManager.createMemento(article, MementoType.CREATE);

        // add comment
        var comment = new Comment();
        comment.setName("Comment 1");
        comment.setArticle(article);
        comment = commentManager.createComment(article, comment);

        article = articleManager.findById(article.getId());
        articleMementoManager.createMemento(article, MementoType.UPDATE);

        // add file
        var file = new File();
        file.setName("File 1");
        commentManager.addFile(comment, file);

        article = articleManager.findById(article.getId());
        articleMementoManager.createMemento(article, MementoType.UPDATE);

        article = articleManager.findById(article.getId());
        var mementos = articleMementoManager.getMementos(article);

        assertThat(mementos).hasSize(3);
    }

//    @Test
//    void testShouldUndoChanges() {
//
//        // add article
//
//        var article = new Article();
//        article.setName("Test");
//        article = articleManager.createArticle(article);
//        assertThat(mementoRepository.findAll()).hasSize(1);
//
//        article.setName("Foo");
//        article = articleManager.updateArticle(article);
//        assertThat(mementoRepository.findAll()).hasSize(2);
//
//        // add comment
//
//        var comment = new Comment();
//        comment.setName("Test");
//        comment.setArticle(article);
//
//        article = articleManager.createComment(article, comment);
//        assertThat(mementoRepository.findAll()).hasSize(3);
//
//        comment = commentRepository.findById(comment.getId()).orElseThrow();
//        assertThat(comment.getName()).isEqualTo("Test");
//
//        // change comment and undo action
//
//        comment.setName("Foo");
//        articleManager.updateComment(article, comment);
//        assertThat(mementoRepository.findAll()).hasSize(4);
//
//        comment = commentRepository.findById(comment.getId()).orElseThrow();
//        assertThat(comment.getName()).isEqualTo("Foo");
//
//        articleManager.undo(article);
//        assertThat(mementoRepository.findAll()).hasSize(5);
//
//        comment = commentRepository.findById(comment.getId()).orElseThrow();
//        assertThat(comment.getName()).isEqualTo("Test");
//
//        // change article and undo
//
//        article.setName("Change!");
//        articleManager.updateArticle(article);
//        assertThat(mementoRepository.findAll()).hasSize(6);
//
//        articleManager.undo(article);
//        assertThat(mementoRepository.findAll()).hasSize(7);
//
//        article = articleRepository.findById(article.getId()).orElseThrow();
//        assertThat(article.getName()).isEqualTo("Foo");
//
//        // add another new comment and undo
//
//        comment = new Comment();
//        comment.setName("BarBaz");
//        comment.setArticle(article);
//
//        article = articleManager.createComment(article, comment);
//        assertThat(mementoRepository.findAll()).hasSize(8);
//
//        article = articleRepository.findById(article.getId()).orElseThrow();
//        assertThat(article.getComments()).hasSize(2);
//
//        articleManager.undo(article);
//        assertThat(mementoRepository.findAll()).hasSize(9);
//
//        article = articleRepository.findById(article.getId()).orElseThrow();
//        assertThat(article.getComments()).hasSize(1);
//
//        // delete remaining comment and undo
//
//        comment = article.getComments().stream().findFirst().orElseThrow();
//        comment = commentRepository.findById(comment.getId()).orElseThrow();
//
//        article = articleManager.deleteComment(article, comment);
//        assertThat(mementoRepository.findAll()).hasSize(10);
//
//        article = articleRepository.findById(article.getId()).orElseThrow();
//        assertThat(article.getComments()).isEmpty();
//
//        articleManager.undo(article);
//        assertThat(mementoRepository.findAll()).hasSize(11);
//
//        article = articleRepository.findById(article.getId()).orElseThrow();
//        assertThat(article.getComments()).hasSize(1);
//    }
}
