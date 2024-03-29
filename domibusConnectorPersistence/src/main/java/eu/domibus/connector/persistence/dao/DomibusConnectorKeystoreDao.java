package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorKeystore;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DomibusConnectorKeystoreDao extends CrudRepository<PDomibusConnectorKeystore, Long> {
    Optional<PDomibusConnectorKeystore> findByUuid(String uuid);
}
