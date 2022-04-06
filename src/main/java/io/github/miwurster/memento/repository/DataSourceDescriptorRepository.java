package io.github.miwurster.memento.repository;

import io.github.miwurster.memento.entity.DataSourceDescriptor;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSourceDescriptorRepository extends JpaRepository<DataSourceDescriptor, UUID>,
    RevisionRepository<DataSourceDescriptor, UUID, Integer> {


    List<DataSourceDescriptor> findAllByDataPoolId(UUID dataPoolId);
}
