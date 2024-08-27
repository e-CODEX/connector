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

import eu.domibus.connector.persistence.model.PDomibusConnectorMessageError;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the Data Access Object (DAO) for the DomibusConnectorMessageError
 * entity. It provides methods for CRUD operations (Create, Read, Update, Delete) on the
 * DomibusConnectorMessageError entity. It extends the CrudRepository interface which provides
 * generic implementations of these operations.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorMessageErrorDao
    extends CrudRepository<PDomibusConnectorMessageError, Long> {
    List<PDomibusConnectorMessageError> findByMessageId(Long messageId);
}
