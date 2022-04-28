package io.github.miwurster.memento.service;

import io.github.miwurster.memento.entity.Comment;
import io.github.miwurster.memento.model.CommentMemento;
import io.github.miwurster.memento.model.MementoType;
import io.github.miwurster.memento.repository.CommentMementoRepository;
import io.github.miwurster.memento.repository.CommentRepository;
import io.github.miwurster.memento.repository.FileRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentMementoManager implements MementoManager<CommentMemento, Comment> {

    private final CommentRepository commentRepository;

    private final FileRepository fileRepository;

    private final CommentMementoRepository commentMementoRepository;

    @Override
    public List<CommentMemento> getMementos(Comment comment) {
        return commentMementoRepository.findAllByCommentId(comment.getId());
    }

    @Override
    @Transactional
    public CommentMemento createMemento(Comment comment, MementoType type) {
        var commentRev = commentRepository.findLastChangeRevision(comment.getId()).orElseThrow();
        var filesRev = comment.getFiles().stream()
            .map(f -> fileRepository.findLastChangeRevision(f.getId()).orElseThrow())
            .collect(Collectors.toList());
        CommentMemento memento = new CommentMemento();
        memento.setType(type);
        memento.setComment(createEntityRevision(commentRev));
        memento.setFiles(filesRev.stream().map(this::createEntityRevision).collect(Collectors.toList()));
        return commentMementoRepository.save(memento);
    }

    @Override
    @Transactional
    public Comment revertTo(CommentMemento memento) {
        return null;
    }
}
