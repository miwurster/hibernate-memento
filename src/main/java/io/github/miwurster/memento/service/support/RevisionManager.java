package io.github.miwurster.memento.service.support;

public interface RevisionManager<R, E> extends AbstractMementoManager {

    R createRevision(E entity);
}
