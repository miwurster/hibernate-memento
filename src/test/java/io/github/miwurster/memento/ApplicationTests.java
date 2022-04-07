package io.github.miwurster.memento;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.miwurster.memento.entity.Article;
import io.github.miwurster.memento.entity.Comment;
import io.github.miwurster.memento.entity.DataPool;
import io.github.miwurster.memento.entity.DataSourceDescriptor;
import io.github.miwurster.memento.entity.File;
import io.github.miwurster.memento.repository.ArticleMementoRepository;
import io.github.miwurster.memento.repository.ArticleRepository;
import io.github.miwurster.memento.repository.CommentRepository;
import io.github.miwurster.memento.repository.DataPoolMementoRepository;
import io.github.miwurster.memento.repository.DataPoolRepository;
import io.github.miwurster.memento.repository.DataSourceDescriptorRepository;
import io.github.miwurster.memento.repository.FileRepository;
import io.github.miwurster.memento.service.ArticleManager;
import io.github.miwurster.memento.service.DataPoolManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.history.RevisionMetadata.RevisionType;

@SpringBootTest
class ApplicationTests {

    @Autowired
    FileRepository fileRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ArticleMementoRepository mementoRepository;
    @Autowired
    private ArticleManager articleManager;
    @Autowired
    private DataPoolManager dataPoolManager;
    @Autowired
    private DataPoolMementoRepository dataPoolMementoRepository;
    @Autowired
    private DataSourceDescriptorRepository dataSourceDescriptorRepository;
    @Autowired
    private DataPoolRepository dataPoolRepository;

    @Test
    void testShouldCreateDatapool() {

        //ARRANGE

        //persist data pool
        var pool = buildPool();

        //ACT
        //create pool with memento
        pool = dataPoolManager.createDataPool(pool);

        //ASSERT
        assertThat(dataPoolMementoRepository.findAll()).hasSize(1);
    }

    @Test
    void testShouldUpdateDataPool() {

        //ARRANGE
        var pool = buildPool();
        pool = dataPoolManager.createDataPool(pool);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(1);

        //ACT
        pool.setName("New funky name");
        pool.setLicenceType("pay per click");
        pool = dataPoolManager.updateDataPool(pool);

        //ASSERT
        assertThat(pool.getName()).isEqualTo("New funky name");
        assertThat(pool.getLicenceType()).isEqualTo("pay per click");
        assertThat(dataPoolMementoRepository.findAll()).hasSize(2);

    }

    @Test
    void testShouldDeleteDataPool() {

        //ARRANGE

        //persist simple pool
        var pool = buildPool();
        pool = dataPoolManager.createDataPool(pool);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(1);

        //ACT
        pool = dataPoolManager.deleteDataPool(pool);

        //ASSERT
        assertThat(dataPoolMementoRepository.findAll()).hasSize(2);
        assertThat(dataPoolRepository.findAll()).isEmpty();

    }


    @Test
    void testShouldCreateDataSourceDescriptor() {

        //ARRANGE
        var pool = buildPool();
        pool = dataPoolManager.createDataPool(pool);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(1);

        var descriptor = buildDescriptor();

        //ACT
        pool = dataPoolManager.createDescriptor(pool, descriptor);

        //ASSERT
        assertThat(pool.getDataSourceDescriptors()).contains(descriptor);
        assertThat(dataSourceDescriptorRepository.findAll()).hasSize(1);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(2);

    }

    @Test
    void testShouldUpdateDataSourceDescriptor() {

        //ARRANGE
        var pool = buildPool();
        pool = dataPoolManager.createDataPool(pool);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(1);

        var descriptor = buildDescriptor();
        descriptor.setDescription("New description");
        pool = dataPoolManager.createDescriptor(pool, descriptor);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(2);

        //ACT
        pool = dataPoolManager.updateDataSourceDescriptor(pool, descriptor);

        //ASSERT
        assertThat(pool.getDataSourceDescriptors()).contains(descriptor);
        assertThat(dataSourceDescriptorRepository.findAll()).hasSize(1);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(3);
        assertThat(descriptor.getDescription()).isEqualTo("New description");

    }

    @Test
    void testShouldRemoveDataSourceDescriptor() {

        //ARRANGE
        var pool = buildPool();
        pool = dataPoolManager.createDataPool(pool);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(1);

        var descriptor = buildDescriptor();
        pool = dataPoolManager.createDescriptor(pool, descriptor);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(2);

        //ACT
        pool = dataPoolManager.deleteDataSourceDescriptor(pool, descriptor);

        //ASSERT
        pool = dataPoolRepository.findById(pool.getId()).orElseThrow();
        assertThat(pool.getDataSourceDescriptors()).isEmpty();
        assertThat(dataSourceDescriptorRepository.findAll()).isEmpty();
        assertThat(dataPoolMementoRepository.findAll()).hasSize(3);

    }

    @Test
    void testShouldUpdateFile() {

        //ARRANGE

        //create data pool
        var pool = buildPool();
        pool = dataPoolManager.createDataPool(pool);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(1);

        //create data source descriptor
        var descriptor = buildDescriptor();
        pool = dataPoolManager.createDescriptor(pool, descriptor);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(2);

        //create file
        var file = buildFile();
        file.setFileUrl("www.coolFile.com");
        pool = dataPoolManager.createFile(pool, descriptor, file);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(3);

        //ASSERT
        pool = dataPoolRepository.findById(pool.getId()).orElseThrow();
        assertThat(pool.getDataSourceDescriptors().contains(descriptor)).isTrue();

        descriptor = dataSourceDescriptorRepository.findById(descriptor.getId()).orElseThrow();
        assertThat(descriptor.getFiles()).hasSize(1);

        file = fileRepository.findById(file.getId()).orElseThrow();
        assertThat(file.getFileUrl()).isEqualTo("www.coolFile.com");

    }

    @Test
    void testShouldDeleteFile() {

        //ARRANGE

        //create data pool
        var pool = buildPool();
        pool = dataPoolManager.createDataPool(pool);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(1);

        //create data source descriptor
        var descriptor = buildDescriptor();
        pool = dataPoolManager.createDescriptor(pool, descriptor);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(2);

        //create file
        var file = buildFile();
        pool = dataPoolManager.createFile(pool, descriptor, file);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(3);

        pool = dataPoolManager.deleteFile(pool, descriptor, file);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(4);

        //ASSERT
        pool = dataPoolRepository.findById(pool.getId()).orElseThrow();
        assertThat(pool.getDataSourceDescriptors().contains(descriptor)).isTrue();

        descriptor = dataSourceDescriptorRepository.findById(descriptor.getId()).orElseThrow();
        assertThat(descriptor.getFiles()).isEmpty();

    }

    @Test
    void undoChangesInDataPool() {

        //create pool
        var pool = buildPool();
        pool = dataPoolManager.createDataPool(pool);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(1);

        // change pool
        pool.setMetadata("Wrong data");
        pool.setDescription("Wrong description");
        pool = dataPoolManager.updateDataPool(pool);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(2);

        // undo
        pool = dataPoolManager.undoDataPoolChanges(pool);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(3);
        assertThat(pool.getMetadata()).isEqualTo("metadata");
        assertThat(pool.getDescription()).isEqualTo("description");

    }

    @Test
    void testShouldCreateDatapoolWithDescriptorAndFile() {

        //ARRANGE

        //create data pool
        var pool = buildPool();
        pool = dataPoolManager.createDataPool(pool);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(1);

        //create data source descriptor
        var descriptor = buildDescriptor();
        pool = dataPoolManager.createDescriptor(pool, descriptor);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(2);

        //create file
        var file = buildFile();
        pool = dataPoolManager.createFile(pool, descriptor, file);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(3);

        //ASSERT
        pool = dataPoolRepository.findById(pool.getId()).orElseThrow();
        assertThat(pool.getDataSourceDescriptors().contains(descriptor)).isTrue();

        descriptor = dataSourceDescriptorRepository.findById(descriptor.getId()).orElseThrow();
        assertThat(descriptor.getFiles()).hasSize(1);

    }

    @Test
    //TODO: fix
    void testShouldDeleteDataPoolWithDescriptorAndFiles() {

        //ARRANGE

        //persist pool
        var pool = buildPool();
        pool = dataPoolManager.createDataPool(pool);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(1);

        //persist data source descriptor
        var descriptor = buildDescriptor();
        pool = dataPoolManager.createDescriptor(pool, descriptor);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(2);

        //persist file
        var file = buildFile();
        pool = dataPoolManager.createFile(pool, descriptor, file);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(3);

        //ACT
        pool = dataPoolManager.deleteDataPool(pool);
        assertThat(dataPoolMementoRepository.findAll()).hasSize(4);

        //ASSERT
        assertThat(dataPoolRepository.findAll()).isEmpty();

    }

    @Test
    @Disabled
    void testShouldRestoreDeletedArticle() {
        // add article
        var article = new Article();
        article.setName("ArticleFoo");
        article = articleManager.createArticle(article);

        // add comment
        var comment = new Comment();
        comment.setName("Comment 1");
        comment.setArticle(article);
        article = articleManager.createComment(article, comment);

        // delete article
        article = articleManager.deleteArticle(article);

        // undo and assert
        article = articleManager.undo(article);

        assertThat(article.getName()).isEqualTo("ArticleFoo");
        assertThat(article.getComments()).hasSize(1);
    }

    @Test
    void testShouldRestoreCommentWithSameIdBeforeDelete() {
        // add article
        var article = new Article();
        article.setName("ArticleFoo");
        article = articleManager.createArticle(article);

        // add comment
        var comment = new Comment();
        comment.setName("Comment 1");
        comment.setArticle(article);
        article = articleManager.createComment(article, comment);

        // remember to assert later
        var deletedComment = article.getComments().stream().findFirst().orElseThrow();

        // delete comment
        article = articleManager.deleteComment(article, comment);

        // undo and assert
        article = articleManager.undo(article);
        var restoredComment = article.getComments().stream().findFirst().orElseThrow();

        assertThat(restoredComment.getId()).isEqualTo(deletedComment.getId());
        assertThat(restoredComment).isEqualTo(deletedComment);
    }

    @Test
    void testShouldUndoChanges() {

        // add article

        var article = new Article();
        article.setName("Test");
        article = articleManager.createArticle(article);
        assertThat(mementoRepository.findAll()).hasSize(1);

        article.setName("Foo");
        article = articleManager.updateArticle(article);
        assertThat(mementoRepository.findAll()).hasSize(2);

        // add comment

        var comment = new Comment();
        comment.setName("Test");
        comment.setArticle(article);

        article = articleManager.createComment(article, comment);
        assertThat(mementoRepository.findAll()).hasSize(3);

        comment = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(comment.getName()).isEqualTo("Test");

        // change comment and undo action

        comment.setName("Foo");
        articleManager.updateComment(article, comment);
        assertThat(mementoRepository.findAll()).hasSize(4);

        comment = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(comment.getName()).isEqualTo("Foo");

        articleManager.undo(article);
        assertThat(mementoRepository.findAll()).hasSize(5);

        comment = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(comment.getName()).isEqualTo("Test");

        // change article and undo

        article.setName("Change!");
        articleManager.updateArticle(article);
        assertThat(mementoRepository.findAll()).hasSize(6);

        articleManager.undo(article);
        assertThat(mementoRepository.findAll()).hasSize(7);

        article = articleRepository.findById(article.getId()).orElseThrow();
        assertThat(article.getName()).isEqualTo("Foo");

        // add another new comment and undo

        comment = new Comment();
        comment.setName("BarBaz");
        comment.setArticle(article);

        article = articleManager.createComment(article, comment);
        assertThat(mementoRepository.findAll()).hasSize(8);

        article = articleRepository.findById(article.getId()).orElseThrow();
        assertThat(article.getComments()).hasSize(2);

        articleManager.undo(article);
        assertThat(mementoRepository.findAll()).hasSize(9);

        article = articleRepository.findById(article.getId()).orElseThrow();
        assertThat(article.getComments()).hasSize(1);

        // delete remaining comment and undo

        comment = article.getComments().stream().findFirst().orElseThrow();
        comment = commentRepository.findById(comment.getId()).orElseThrow();

        article = articleManager.deleteComment(article, comment);
        assertThat(mementoRepository.findAll()).hasSize(10);

        article = articleRepository.findById(article.getId()).orElseThrow();
        assertThat(article.getComments()).isEmpty();

        articleManager.undo(article);
        assertThat(mementoRepository.findAll()).hasSize(11);

        article = articleRepository.findById(article.getId()).orElseThrow();
        assertThat(article.getComments()).hasSize(1);
    }

    @Test
    void testShouldSaveAndLoadBasicEntities() {
        // add article
        var article = new Article();
        article.setName("Test");
        article = articleRepository.save(article);
        assertThat(articleRepository.count()).isPositive();
        var articleRev = articleRepository.findLastChangeRevision(article.getId()).orElseThrow();
        assertThat(articleRev.getMetadata().getRevisionType()).isEqualTo(RevisionType.INSERT);
        assertThat(articleRepository.findRevisions(article.getId()).getContent()).hasSize(1);

        // add comment
        var comment = new Comment();
        comment.setName("Test");
        comment.setArticle(article);
        comment = commentRepository.save(comment);
        assertThat(commentRepository.count()).isPositive();
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

    private DataPool buildPool() {
        var dataPool = new DataPool();
        dataPool.setName("Test pool");
        dataPool.setShortDescription("short description");
        dataPool.setDescription("description");
        dataPool.setLicenceType("Free");
        dataPool.setMetadata("metadata");
        dataPool.setCreatedAt(LocalDateTime.now());
        return dataPool;
    }

    private DataSourceDescriptor buildDescriptor() {
        var descriptor = new DataSourceDescriptor();
        descriptor.setName("Data Source Descriptor");
        descriptor.setDescription("description");
        descriptor.setCreatedAt(LocalDateTime.now());
        return descriptor;
    }

    private File buildFile() {
        var file = new File();
        file.setName("new file");
        file.setMimeType("JPEG");
        file.setFileUrl("www.test.com");
        file.setCreatedAt(LocalDateTime.now());
        return file;
    }
}
