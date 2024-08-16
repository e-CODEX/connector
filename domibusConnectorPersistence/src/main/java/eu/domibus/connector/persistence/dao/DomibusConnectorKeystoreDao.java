/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
