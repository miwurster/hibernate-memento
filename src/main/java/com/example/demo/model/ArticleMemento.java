package com.example.demo.model;

import com.example.demo.entity.Article;
import com.example.demo.entity.Comment;
import java.util.List;
import lombok.Data;

@Data
public class ArticleMemento implements Memento {

    private EntityRevision<Article> article;

    private List<EntityRevision<Comment>> comments;
}
