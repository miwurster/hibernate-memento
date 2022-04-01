package io.github.miwurster.memento.model;

import java.util.List;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ArticleMemento implements Memento {

    private MementoType type;

    private EntityRevision article;

    private List<EntityRevision> comments;
}
