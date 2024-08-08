/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorBigData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the Data Access Object (DAO) for the Domibus connector big data table.
 * It provides methods for CRUD operations on the table and extends the Spring Data CrudRepository
 * interface.
 *
 * <p>This interface is used by the LargeFilePersistenceServiceJpaImpl class, which uses the
 * bigDataDao field to perform operations on the Domibus connector big data table.
 */
@Repository
public interface DomibusConnectorBigDataDao extends CrudRepository<PDomibusConnectorBigData, Long> {
}
