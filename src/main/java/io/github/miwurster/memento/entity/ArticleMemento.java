package io.github.miwurster.memento.entity;

import io.github.miwurster.memento.entity.memento.CommentRevision;
import io.github.miwurster.memento.entity.memento.EntityRevision;
import io.github.miwurster.memento.entity.memento.MementoType;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ArticleMemento extends Memento {

    @Enumerated(EnumType.STRING)
    private MementoType type;

    private UUID entityId;

    private Integer revisionNumber;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private ArticleMemento.Value value;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Value {

        private List<CommentRevision> commentRevisions;
    }
}
