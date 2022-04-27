package io.github.miwurster.memento.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentMemento extends Memento {

    @Enumerated(EnumType.STRING)
    private MementoType type;

    @OneToOne
    @JoinColumn(name = "comment_memento__comment")
    private EntityRevision comment;

    @OneToMany
    @JoinColumn(name = "comment_memento__files")
    private List<EntityRevision> files = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
