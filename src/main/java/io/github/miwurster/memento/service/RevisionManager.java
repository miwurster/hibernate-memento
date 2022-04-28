package io.github.miwurster.memento.service;

public interface RevisionManager<R, E> extends AbstractMementoManager {

    R createRevision(E entity);
}
