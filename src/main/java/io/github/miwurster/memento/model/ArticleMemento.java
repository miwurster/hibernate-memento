package io.github.miwurster.memento.model;

import java.util.List;
import javax.persistence.CascadeType;
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
public class ArticleMemento extends Memento {

    @Enumerated(EnumType.STRING)
    private MementoType type;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "article_memento__article")
    private EntityRevision article;

    @OneToMany
    @JoinColumn(name = "article_memento__comment_mementos")
    private List<CommentMemento> commentMementos;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
