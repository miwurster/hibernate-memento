package io.github.miwurster.memento;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.miwurster.memento.entity.Article;
import io.github.miwurster.memento.entity.Comment;
import io.github.miwurster.memento.repository.ArticleRepository;
import io.github.miwurster.memento.repository.CommentRepository;
import io.github.miwurster.memento.service.ArticleManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.history.RevisionMetadata.RevisionType;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleManager articleManager;

    @Test
    void testShouldUseMementoPattern() {
        // add article
        var article = new Article();
        article.setName("Test");
        article = articleManager.saveArticle(article);
        assertThat(articleManager.getMementoRepository()).hasSize(1);

        article.setName("Foo");
        article = articleManager.updateArticle(article);
        assertThat(articleManager.getMementoRepository()).hasSize(2);

        var comment = new Comment();
        comment.setName("Test");
        comment.setArticle(article);

        article = articleManager.addComment(article, comment);
        assertThat(articleManager.getMementoRepository()).hasSize(3);

        comment.setName("Foo");
        articleManager.updateComment(article, comment);
        assertThat(articleManager.getMementoRepository()).hasSize(4);

        articleManager.undo();
        assertThat(articleManager.getMementoRepository()).hasSize(5);

        var test = articleManager.getMementoRepository();
        System.out.println(test);
    }

    @Test
    void testShouldSaveAndLoadBasicEntities() {
        // add article
        var article = new Article();
        article.setName("Test");
        article = articleRepository.save(article);
        assertThat(articleRepository.count()).isGreaterThanOrEqualTo(1);
        var articleRev = articleRepository.findLastChangeRevision(article.getId()).orElseThrow();
        assertThat(articleRev.getMetadata().getRevisionType()).isEqualTo(RevisionType.INSERT);
        assertThat(articleRepository.findRevisions(article.getId()).getContent()).hasSize(1);

        // add comment
        var comment = new Comment();
        comment.setName("Test");
        comment.setArticle(article);
        comment = commentRepository.save(comment);
        assertThat(commentRepository.count()).isGreaterThanOrEqualTo(1);
        var commentRev = commentRepository.findLastChangeRevision(comment.getId()).orElseThrow();
        assertThat(commentRev.getMetadata().getRevisionType()).isEqualTo(RevisionType.INSERT);
        assertThat(commentRepository.findRevisions(comment.getId()).getContent()).hasSize(1);

        // article should also get an update
        articleRev = articleRepository.findLastChangeRevision(article.getId()).orElseThrow();
        assertThat(articleRev.getMetadata().getRevisionType()).isEqualTo(RevisionType.UPDATE);
        assertThat(articleRepository.findRevisions(article.getId()).getContent()).hasSize(2);

        // update comment
        comment.setName("FooBarBazQuxDoo");
        comment = commentRepository.save(comment);

        // comment is updated
        commentRev = commentRepository.findLastChangeRevision(comment.getId()).orElseThrow();
        assertThat(commentRev.getMetadata().getRevisionType()).isEqualTo(RevisionType.UPDATE);
        assertThat(commentRepository.findRevisions(comment.getId()).getContent()).hasSize(2);

        // article has still only 2 changes
        articleRev = articleRepository.findLastChangeRevision(article.getId()).orElseThrow();
        assertThat(articleRev.getMetadata().getRevisionType()).isEqualTo(RevisionType.UPDATE);
        assertThat(articleRepository.findRevisions(article.getId()).getContent()).hasSize(2);

        // change article
        article = articleRepository.findById(article.getId()).orElseThrow();
        article.setName("FooBarBaz");
        article = articleRepository.save(article);

        // article has 3 changes now
        articleRev = articleRepository.findLastChangeRevision(article.getId()).orElseThrow();
        assertThat(articleRev.getMetadata().getRevisionType()).isEqualTo(RevisionType.UPDATE);
        assertThat(articleRepository.findRevisions(article.getId()).getContent()).hasSize(3);

        // comment has still 2 changes
        commentRev = commentRepository.findLastChangeRevision(comment.getId()).orElseThrow();
        assertThat(commentRev.getMetadata().getRevisionType()).isEqualTo(RevisionType.UPDATE);
        assertThat(commentRepository.findRevisions(comment.getId()).getContent()).hasSize(2);

        // delete comment
        commentRepository.delete(comment);

        // comment has 3 changes with delete
        commentRev = commentRepository.findLastChangeRevision(comment.getId()).orElseThrow();
        assertThat(commentRev.getMetadata().getRevisionType()).isEqualTo(RevisionType.DELETE);
        assertThat(commentRepository.findRevisions(comment.getId()).getContent()).hasSize(3);

        // article has 4 changes now
        articleRev = articleRepository.findLastChangeRevision(article.getId()).orElseThrow();
        assertThat(articleRev.getMetadata().getRevisionType()).isEqualTo(RevisionType.UPDATE);
        assertThat(articleRepository.findRevisions(article.getId()).getContent()).hasSize(4);

        /*
         * Findings:
         * (1) Envers creates also a new revision of Article if a new Comment entity is stored.
         * (2) Envers also creates a new revision of Article if a Comment entity is deleted.
         * (3) However, Envers does not track any revision if a child entity of Article, i.e., Comment, is just changed.
         * (4) So, IMHO we therefore require a layer on top to also track such changes on child entities.
         */
    }

    @Test
    void contextLoads() {
    }
}
