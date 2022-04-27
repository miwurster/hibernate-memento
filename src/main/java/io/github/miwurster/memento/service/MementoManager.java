package io.github.miwurster.memento.service;

import io.github.miwurster.memento.model.MementoType;
import java.util.List;

public interface MementoManager<M, E> {

    List<M> getMementos(E entity);

    M createMemento(E entity, MementoType type);

    E revertTo(M memento);
}
