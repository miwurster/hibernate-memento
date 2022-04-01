package io.github.miwurster.memento.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityRevision {

    private Long entityId;

    private Integer revisionNumber;
}
