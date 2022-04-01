package io.github.miwurster.memento;

import io.github.miwurster.memento.repository.MementoRepository;
import io.github.miwurster.memento.service.ArticleManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;

@SpringBootApplication
@RequiredArgsConstructor
@EnableEnversRepositories
public class Application implements CommandLineRunner {

    private final ArticleManager articleManager;

    private final MementoRepository mementoRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Used to populate some state into the H2 database. Once the program has been stopped, one can investigate the result in an appropriate database
     * viewer, e.g., the IntelliJ IDEA database plugin.
     */
    @Override
    public void run(String... args) {
        // var article = articleManager.saveArticle(new Article("Article 1", new HashSet<>()));
        // articleManager.addComment(article, new Comment("Comment 1", article));
    }
}
