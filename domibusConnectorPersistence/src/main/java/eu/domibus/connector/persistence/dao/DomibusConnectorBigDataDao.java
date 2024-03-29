package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorBigData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DomibusConnectorBigDataDao extends CrudRepository<PDomibusConnectorBigData, Long> {
}
