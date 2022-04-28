package io.github.miwurster.memento.entity.memento;

import java.util.ArrayList;
import java.util.List;
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
public class CommentRevision {

    private UUID entityId;

    private Integer revisionNumber;

    private List<EntityRevision> files = new ArrayList<>();
}
