package io.github.miwurster.memento.entity.support;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;

public class IdGenerator extends UUIDGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object o) throws HibernateException {
        if (o instanceof PersistentObject) {
            var object = (PersistentObject) o;
            if (object.getId() != null) {
                return object.getId();
            }
        }
        return super.generate(session, o);
    }
}
