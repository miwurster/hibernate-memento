package io.github.miwurster.memento.repository;

import io.github.miwurster.memento.model.Memento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MementoRepository extends JpaRepository<Memento, Long> {

}
