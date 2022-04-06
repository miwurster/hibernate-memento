package io.github.miwurster.memento.service;

import io.github.miwurster.memento.entity.DataPool;
import io.github.miwurster.memento.entity.DataSourceDescriptor;
import io.github.miwurster.memento.entity.PersistentObject;
import io.github.miwurster.memento.model.DataPoolMemento;
import io.github.miwurster.memento.model.DataSourceDescriptorMemento;
import io.github.miwurster.memento.model.EntityRevision;
import io.github.miwurster.memento.model.MementoType;
import io.github.miwurster.memento.repository.DataPoolMementoRepository;
import io.github.miwurster.memento.repository.DataPoolRepository;
import io.github.miwurster.memento.repository.DataSourceDescriptorMementoRepository;
import io.github.miwurster.memento.repository.DataSourceDescriptorRepository;
import io.github.miwurster.memento.repository.FileRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataPoolManager {

    private final DataPoolRepository dataPoolRepository;

    private final DataPoolMementoRepository dataPoolMementoRepository;

    private final DataSourceDescriptorRepository dataSourceDescriptorRepository;

    private final FileRepository fileRepository;

    private final DataSourceDescriptorMementoRepository dataSourceDescriptorMementoRepository;

    public DataPool createDataPool(DataPool dataPool) {

        var savedDataPool = dataPoolRepository.save(dataPool);

        var dataPoolMemento = createDataPoolMemento(savedDataPool, MementoType.CREATE);
        dataPoolMementoRepository.save(dataPoolMemento);

        return savedDataPool;
    }

    public DataPool updateDataPool(DataPool updatedDataPool) {

        var savedDataPool = dataPoolRepository.findById(updatedDataPool.getId()).orElseThrow();
        savedDataPool.setName(updatedDataPool.getName());
        savedDataPool.setShortDescription(updatedDataPool.getShortDescription());
        savedDataPool.setDescription(updatedDataPool.getDescription());
        savedDataPool.setLicenceType(updatedDataPool.getLicenceType());
        savedDataPool.setMetadata(updatedDataPool.getMetadata());

        var dataPoolMemento = createDataPoolMemento(savedDataPool, MementoType.UPDATE);
        dataPoolMementoRepository.save(dataPoolMemento);

        return savedDataPool;
    }

    public DataPool deleteDataPool(DataPool dataPoolToDelete) {

        //fetch data pool
        var savedDataPool = dataPoolRepository.findById(dataPoolToDelete.getId()).orElseThrow();

        // fetch DataSourceDescriptors
        var descriptors = savedDataPool.getDataSourceDescriptors();

        // iterate over descriptors and remove the files
        for (DataSourceDescriptor descriptor : descriptors) {
            fileRepository.deleteAll(descriptor.getFiles());
        }

        // remove descriptors
        dataSourceDescriptorRepository.deleteAll(descriptors);

        //remove pool
        dataPoolRepository.delete(savedDataPool);

        // create memento
        var dataPoolMemento = createDataPoolMemento(savedDataPool, MementoType.DELETE);
        dataPoolMementoRepository.save(dataPoolMemento);

        return savedDataPool;
    }

    public DataPool addDataSourceDescriptorToDataPool(DataPool dataPool, DataSourceDescriptor dataSourceDescriptor) {

        var persistedDataPool = dataPoolRepository.findById(dataPool.getId()).orElseThrow();
        persistedDataPool.setDataSourceDescriptors(dataSourceDescriptor.getDataPool().getDataSourceDescriptors());

        dataSourceDescriptorRepository.save(dataSourceDescriptor);

        persistedDataPool = dataPoolRepository.findById(dataPool.getId()).orElseThrow();
        var dataPoolMemento = createDataPoolMemento(persistedDataPool, MementoType.UPDATE);
        dataPoolMementoRepository.save(dataPoolMemento);

        return persistedDataPool;
    }

    public DataPool updateDataSourceDescriptor(DataPool dataPool, DataSourceDescriptor dataSourceDescriptor) {

        var persistedDataPool = dataPoolRepository.findById(dataPool.getId()).orElseThrow();
        var persistedDataSourceDescriptor = dataSourceDescriptorRepository.findById(
            dataSourceDescriptor.getId()).orElseThrow();

        if (persistedDataPool.getDataSourceDescriptors().contains(persistedDataSourceDescriptor)) {
            persistedDataSourceDescriptor.setName(dataSourceDescriptor.getName());
            persistedDataSourceDescriptor.setDescription(dataSourceDescriptor.getDescription());
            dataSourceDescriptorRepository.save(persistedDataSourceDescriptor);
        }

        persistedDataPool = dataPoolRepository.findById(dataPool.getId()).orElseThrow();
        var dataPoolMemento = createDataPoolMemento(persistedDataPool, MementoType.UPDATE);
        dataPoolMementoRepository.save(dataPoolMemento);

        return persistedDataPool;
    }

    private DataPoolMemento createDataPoolMemento(DataPool dataPool, MementoType type) {

        var poolRev = dataPoolRepository.findLastChangeRevision(dataPool.getId()).orElseThrow();
        var dataSourceDescriptor = dataPool.getDataSourceDescriptors();

        List<DataSourceDescriptorMemento> descriptorMementos = new ArrayList<>();

        for (DataSourceDescriptor descriptor : dataSourceDescriptor) {

            var descriptorRev
                = dataSourceDescriptorRepository.findLastChangeRevision(descriptor.getId()).orElseThrow();

            var filesRev = descriptor.getFiles().stream()
                .map(file -> fileRepository.findLastChangeRevision(file.getId()).orElseThrow())
                .collect(Collectors.toList());
        }

        var dataPoolMemento = new DataPoolMemento();
        dataPoolMemento.setType(type);
        dataPoolMemento.setDataPool(createEntityRevision(poolRev));
        dataPoolMemento.setDataSourceDescriptors(descriptorMementos);
        return dataPoolMemento;

    }

    private EntityRevision createEntityRevision(Revision<Integer, ? extends PersistentObject> revision) {
        EntityRevision rev = new EntityRevision();
        rev.setEntityId(revision.getEntity().getId());
        rev.setRevisionNumber(revision.getRequiredRevisionNumber());
        return rev;
    }

}
