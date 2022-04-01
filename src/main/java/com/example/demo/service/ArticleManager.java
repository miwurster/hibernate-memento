package com.example.demo.service;

import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleManager {

    private final ArticleRepository articleRepository;

    private final CommentRepository commentRepository;

//    public void saveState(Article article) {
//
//        articleRepository.findLastChangeRevision()
//
//
//        let snapshot: GameWorldMemento = originator.createMemento()
//        snapshots[identifier] = snapshot
//    }
//
//    public void restoreState(Article article, Integer revisionNumber) {
//        if let snapshot = snapshots[identifier] {
//            originator.apply(memento: snapshot)
//        }
//    }
}
