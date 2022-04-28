package io.github.miwurster.memento.entity.memento;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EntityRevision {

    private UUID entityId;

    private Integer revisionNumber;
}
