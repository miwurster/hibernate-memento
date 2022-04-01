package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.entity.Article;
import com.example.demo.entity.Comment;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void testShouldSaveAndLoadBasicEntities() {
        var article = articleRepository.save(Article.builder().name("Test").build());
        assertThat(articleRepository.count()).isEqualTo(1);

        var comment = commentRepository.save(Comment.builder().name("Test").article(article).build());
        assertThat(commentRepository.count()).isEqualTo(1);

        article = articleRepository.findById(article.getId()).orElseThrow();
        assertThat(article.getComments()).hasSize(1);

        var rev1 = commentRepository.findLastChangeRevision(comment.getId()).orElseThrow();
        assertThat(rev1.getRevisionNumber().orElseThrow()).isNotNull();

        var rev2 = articleRepository.findLastChangeRevision(article.getId()).orElseThrow();
        assertThat(rev2.getRevisionNumber().orElseThrow()).isNotNull();
    }

    @Test
    void testShouldRevertArticle() {
        var article = articleRepository.save(Article.builder().name("Test").build());
        commentRepository.save(Comment.builder().name("Comment 1").article(article).build());
        commentRepository.save(Comment.builder().name("Comment 2").article(article).build());

        article.setName("Foo");
        article = articleRepository.save(article);

        commentRepository.save(Comment.builder().name("Comment 3").article(article).build());

        var revisions = articleRepository.findRevisions(article.getId()).getContent();

        var rev = articleRepository.findRevision(article.getId(), revisions.size() - 2).orElseThrow();

        // find article
        // then update fields

        System.out.println(rev.getEntity());

        // articleRepository.save(rev.getEntity());
    }
}
