package io.github.miwurster.memento;

import io.github.miwurster.memento.entity.Article;
import io.github.miwurster.memento.entity.Comment;
import io.github.miwurster.memento.repository.ArticleRepository;
import io.github.miwurster.memento.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;

@SpringBootApplication
@RequiredArgsConstructor
@EnableEnversRepositories
public class Application implements CommandLineRunner {

    private final ArticleRepository articleRepository;

    private final CommentRepository commentRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Used to populate some state into the H2 database. Once the program has been stopped, one can investigate the result in an appropriate database
     * viewer, e.g., the IntelliJ IDEA database plugin.
     */
    @Override
    public void run(String... args) {

        var article = new Article();
        article.setName("Test");

        // add article
        article = articleRepository.save(article);

        // add comment
        var comment = new Comment();
        comment.setName("Test");
        comment.setArticle(article);
        comment = commentRepository.save(comment);

        // update comment
        comment.setName("FooBarBazQuxDoo");
        comment = commentRepository.save(comment);

        // change article
        article = articleRepository.findById(article.getId()).orElseThrow();
        article.setName("FooBarBaz");
        articleRepository.save(article);

        commentRepository.delete(comment);
    }
}
