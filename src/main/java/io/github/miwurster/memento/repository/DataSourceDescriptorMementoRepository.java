package io.github.miwurster.memento.repository;

import io.github.miwurster.memento.model.DataSourceDescriptorMemento;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSourceDescriptorMementoRepository extends MementoRepository<DataSourceDescriptorMemento> {

    @Query(value = "SELECT d FROM DataSourceDescriptorMemento d WHERE d.dataSourceDescriptor.entityId = ?1 ORDER BY d.dataSourceDescriptor.revisionNumber ASC")
    List<DataSourceDescriptorMemento> findAllByDataSourceDescriptorId(UUID descriptorId);
}
