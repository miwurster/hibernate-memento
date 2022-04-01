package io.github.miwurster.memento.entity;

import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

@Getter
@Setter
@Entity
@RevisionEntity
@NoArgsConstructor
public class RevisionInfo extends DefaultRevisionEntity {

}
