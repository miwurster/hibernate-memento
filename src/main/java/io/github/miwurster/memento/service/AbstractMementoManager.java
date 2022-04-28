package io.github.miwurster.memento.service;

import io.github.miwurster.memento.entity.memento.EntityRevision;
import io.github.miwurster.memento.entity.support.PersistentObject;
import org.springframework.data.history.Revision;

public interface AbstractMementoManager {

    default EntityRevision createEntityRevision(Revision<Integer, ? extends PersistentObject> revision) {
        EntityRevision rev = new EntityRevision();
        rev.setEntityId(revision.getEntity().getId());
        rev.setRevisionNumber(revision.getRequiredRevisionNumber());
        return rev;
    }
}
