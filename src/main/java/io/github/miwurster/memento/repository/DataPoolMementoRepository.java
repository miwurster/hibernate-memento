package io.github.miwurster.memento.repository;

import io.github.miwurster.memento.model.DataPoolMemento;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DataPoolMementoRepository extends MementoRepository<DataPoolMemento> {

    @Query(value = "SELECT dt FROM DataPoolMemento dt WHERE dt.dataPool.entityId = ?1 ORDER BY dt.dataPool.revisionNumber ASC")
    List<DataPoolMemento> findAllByDataPoolId(UUID dataPoolId);
}
