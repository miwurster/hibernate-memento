package io.github.miwurster.memento.repository;

import io.github.miwurster.memento.entity.File;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, UUID>, RevisionRepository<File, UUID, Integer> {

}
