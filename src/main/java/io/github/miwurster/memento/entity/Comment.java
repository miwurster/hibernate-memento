package io.github.miwurster.memento.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Getter
@Setter
@Audited
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends PersistentObject {

    private String name;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ToString.Exclude
    @OneToMany(mappedBy = "comment")
    private List<File> files = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
