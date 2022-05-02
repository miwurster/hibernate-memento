package io.github.miwurster.memento.service;

import io.github.miwurster.memento.entity.Comment;
import io.github.miwurster.memento.entity.File;
import io.github.miwurster.memento.entity.memento.CommentRevision;
import io.github.miwurster.memento.repository.CommentRepository;
import io.github.miwurster.memento.repository.FileRepository;
import io.github.miwurster.memento.service.support.RevisionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.Revision;
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

    @Override
    @Transactional
    public void revertTo(CommentRevision revision) {

        // get revision
        var commentRevision = commentRepository.findRevision(revision.getEntityId(), revision.getRevisionNumber()).orElseThrow();
        // get current version
        var comment = commentRepository.findById(revision.getEntityId()).orElse(commentRevision.getEntity());
        // update properties
        comment.setName(commentRevision.getEntity().getName());
        // save entity
        comment = commentRepository.save(comment);

        // get file revisions
        var fileRevisions = revision.getFiles().stream()
            .map(e -> fileRepository.findRevision(e.getEntityId(), e.getRevisionNumber()).orElseThrow())
            .collect(Collectors.toList());

        // do some fancy logic
        var filesToRestore = fileRevisions.stream().map(Revision::getEntity).collect(Collectors.toList());
        for (File file : filesToRestore) {
            file.setComment(comment);
        }
        List<File> filesToSave = new ArrayList<>();
        List<File> filesToDelete = new ArrayList<>();
        for (File file : comment.getFiles()) {
            if (filesToRestore.contains(file)) {
                var i = filesToRestore.indexOf(file);
                var fileToRestore = filesToRestore.get(i);
                file.setName(fileToRestore.getName());
                filesToSave.add(file);
            } else {
                filesToDelete.add(file);
            }
        }
        for (File file : filesToRestore) {
            if (!comment.getFiles().contains(file)) {
                filesToSave.add(file);
            }
        }
        fileRepository.deleteAll(filesToDelete);
        fileRepository.saveAll(filesToSave);
    }
}
