package io.github.miwurster.memento.service;

import io.github.miwurster.memento.entity.DataPool;
import io.github.miwurster.memento.entity.DataSourceDescriptor;
import io.github.miwurster.memento.entity.File;
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

        var savedDataPool = dataPoolRepository.findById(dataPool.getId()).orElseThrow();

        var dataPoolMemento = createDataPoolMemento(savedDataPool, MementoType.CREATE);
        dataPoolMementoRepository.save(dataPoolMemento);

        return savedDataPool;
    }

    private DataPoolMemento createDataPoolMemento(DataPool dataPool, MementoType type) {

        // collect revisions for data pool, data source descriptor and files
        var pool = dataPoolRepository.findById(dataPool.getId()).orElseThrow();
        var dataSourceDescriptor = dataSourceDescriptorRepository.findAllByDataPoolId(pool.getId());

        List<Revision<Integer, File>> filesRev = new ArrayList<>();
        for (DataSourceDescriptor descriptor : dataSourceDescriptor) {
            filesRev = descriptor.getFiles().stream().map(file -> fileRepository.findLastChangeRevision(file.getId()).orElseThrow())
                .collect(Collectors.toList());
        }

        // System.out.println("Files Revision" + filesRev);

//        var filesRev = dataSourceDescriptor.stream()
//            .map(file -> fileRepository.findLastChangeRevision(file.getId()).orElseThrow())
//            .collect(Collectors.toList());

        var poolRev = dataPoolRepository.findLastChangeRevision(pool.getId()).orElseThrow();
        var descriptorRev = pool.getDataSourceDescriptors().stream()
            .map(descriptor -> dataSourceDescriptorRepository.findLastChangeRevision(descriptor.getId()).orElseThrow())
            .collect(Collectors.toList());

        //create DataSourceDescriptorMemento
        var descriptorMemento = new DataSourceDescriptorMemento();
        descriptorMemento.setType(type);
        descriptorMemento.setDataSourceDescriptor(descriptorRev.stream().map(this::createEntityRevision).findFirst().orElseThrow());
        descriptorMemento.setFiles(filesRev.stream().map(this::createEntityRevision).collect(Collectors.toList()));
        dataSourceDescriptorMementoRepository.save(descriptorMemento);

        List<DataSourceDescriptorMemento> descriptorMementos = new ArrayList<>();
        descriptorMementos.add(descriptorMemento);

        // create dataPoolMemento
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
