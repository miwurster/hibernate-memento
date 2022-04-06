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
public class DataPoolMemento extends Memento {

    @Enumerated(EnumType.STRING)
    private MementoType type;

    @JoinColumn(name = "data_pool_id")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private EntityRevision dataPool;

    @JoinColumn(name = "data_pool__data_source_descriptor_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DataSourceDescriptorMemento> dataSourceDescriptors;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
