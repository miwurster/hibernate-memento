package io.github.miwurster.memento.entity;

import io.github.miwurster.memento.entity.support.PersistentObject;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity
@Audited
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class File extends PersistentObject {

    private String name;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
