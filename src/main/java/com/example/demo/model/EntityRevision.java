package com.example.demo.model;

import lombok.Data;

@Data
public class EntityRevision<T> {

    private Class<T> entityType;

    private Long entityId;

    private Integer revisionNumber;
}
