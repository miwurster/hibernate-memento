package io.github.miwurster.memento.service;

import io.github.miwurster.memento.entity.Article;
import io.github.miwurster.memento.entity.Comment;
import io.github.miwurster.memento.entity.File;
import io.github.miwurster.memento.repository.ArticleRepository;
import io.github.miwurster.memento.repository.CommentRepository;
import io.github.miwurster.memento.repository.FileRepository;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentManager {

    private final ArticleRepository articleRepository;

    private final CommentRepository commentRepository;

    private final FileRepository fileRepository;

    public Comment findById(UUID id) {
        return commentRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Comment createComment(Article article, Comment comment) {
        var persistedArticle = articleRepository.findById(article.getId()).orElseThrow();
        comment.setArticle(persistedArticle);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Article article, Comment comment) {
        var persistedArticle = articleRepository.findById(article.getId()).orElseThrow();
        var persistedComment = commentRepository.findById(comment.getId()).orElseThrow();
        if (persistedArticle.getComments().contains(persistedComment)) {
            persistedComment.setName(comment.getName());
            persistedComment = commentRepository.save(persistedComment);
        }
        return persistedComment;
    }

    @Transactional
    public void deleteComment(Comment comment) {
        var persistedComment = commentRepository.findById(comment.getId()).orElseThrow();
        fileRepository.deleteAll(persistedComment.getFiles());
        commentRepository.delete(persistedComment);
    }

    @Transactional
    public File addFile(Comment comment, File file) {
        var persistedComment = commentRepository.findById(comment.getId()).orElseThrow();
        file.setComment(persistedComment);
        return fileRepository.save(file);
    }

    @Transactional
    public void removeFile(File file) {
        var persistedFile = fileRepository.findById(file.getId()).orElseThrow();
        fileRepository.delete(persistedFile);
    }
}
