package io.github.miwurster.memento.service;

import io.github.miwurster.memento.entity.PersistentObject;
import io.github.miwurster.memento.model.EntityRevision;
import io.github.miwurster.memento.model.MementoType;
import java.util.List;
import org.springframework.data.history.Revision;

public interface MementoManager<M, E> {

    List<M> getMementos(E entity);

    M createMemento(E entity, MementoType type);

    E revertTo(M memento);

    default EntityRevision createEntityRevision(Revision<Integer, ? extends PersistentObject> revision) {
        EntityRevision rev = new EntityRevision();
        rev.setEntityId(revision.getEntity().getId());
        rev.setRevisionNumber(revision.getRequiredRevisionNumber());
        return rev;
    }
}
