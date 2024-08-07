/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
    List<PDomibusConnectorMessageError> findByMessage(Long messageId);
}
