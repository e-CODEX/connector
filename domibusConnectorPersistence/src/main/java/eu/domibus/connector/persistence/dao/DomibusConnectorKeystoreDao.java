/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorKeystore;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * The DomibusConnectorKeystoreDao interface provides methods for CRUD operations on
 * PDomibusConnectorKeystore entities.
 *
 * <p>This interface extends the CrudRepository interface, which provides the basic CRUD
 * operations.
 *
 * <p>The entity class PDomibusConnectorKeystore represents a keystore with its attributes and
 * associations.
 */
@Repository
public interface DomibusConnectorKeystoreDao
    extends CrudRepository<PDomibusConnectorKeystore, Long> {
    Optional<PDomibusConnectorKeystore> findByUuid(String uuid);
}
