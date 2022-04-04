package io.github.miwurster.memento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MementoRepository<T> extends JpaRepository<T, Long> {

}
