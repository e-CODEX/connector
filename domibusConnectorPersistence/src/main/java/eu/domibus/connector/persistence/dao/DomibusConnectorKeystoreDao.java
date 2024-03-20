package eu.domibus.connector.persistence.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.domibus.connector.persistence.model.PDomibusConnectorKeystore;

@Repository
public interface DomibusConnectorKeystoreDao extends CrudRepository<PDomibusConnectorKeystore, Long> {

	public Optional<PDomibusConnectorKeystore> findByUuid(String uuid);
}
