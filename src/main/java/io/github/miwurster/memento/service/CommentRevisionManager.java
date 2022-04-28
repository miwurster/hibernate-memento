package io.github.miwurster.memento.service;

import io.github.miwurster.memento.entity.Comment;
import io.github.miwurster.memento.entity.memento.CommentRevision;
import io.github.miwurster.memento.repository.CommentRepository;
import io.github.miwurster.memento.repository.FileRepository;
import io.github.miwurster.memento.service.support.RevisionManager;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentRevisionManager implements RevisionManager<CommentRevision, Comment> {

    private final CommentRepository commentRepository;

    private final FileRepository fileRepository;

    @Override
    @Transactional
    public CommentRevision createRevision(Comment comment) {
        var commentRev = commentRepository.findLastChangeRevision(comment.getId()).orElseThrow();
        var filesRev = comment.getFiles().stream()
            .map(f -> fileRepository.findLastChangeRevision(f.getId()).orElseThrow())
            .collect(Collectors.toList());
        CommentRevision revision = new CommentRevision();
        revision.setEntityId(commentRev.getEntity().getId());
        revision.setRevisionNumber(commentRev.getRequiredRevisionNumber());
        revision.setFiles(filesRev.stream().map(this::createEntityRevision).collect(Collectors.toList()));
        return revision;
    }
}
