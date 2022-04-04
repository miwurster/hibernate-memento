package io.github.miwurster.memento.model;

import io.github.miwurster.memento.entity.PersistentObject;
import java.util.UUID;
import javax.persistence.Entity;
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
public class EntityRevision extends PersistentObject {

    private UUID entityId;

    private Integer revisionNumber;
}
