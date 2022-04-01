package com.example.demo.entity;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;

@Entity
@Getter
@Setter
@Audited
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Article extends PersistentObject {

    private String name;

    @AuditMappedBy(mappedBy = "article")
    @OneToMany(mappedBy = "article", fetch = FetchType.EAGER)
    private Set<Comment> comments;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
