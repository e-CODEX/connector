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
