package io.github.miwurster.memento.service;

import io.github.miwurster.memento.entity.memento.MementoType;
import java.util.List;

public interface MementoManager<M, E> extends AbstractMementoManager {

    List<M> getMementos(E entity);

    M createMemento(E entity, MementoType type);

    E revertTo(M memento);
}
