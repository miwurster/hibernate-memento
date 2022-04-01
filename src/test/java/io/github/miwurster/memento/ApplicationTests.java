package io.github.miwurster.memento;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.miwurster.memento.entity.Article;
import io.github.miwurster.memento.entity.Comment;
import io.github.miwurster.memento.repository.ArticleRepository;
import io.github.miwurster.memento.repository.CommentRepository;
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
}
