/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorUser;
import eu.domibus.connector.persistence.model.PDomibusConnectorUserPassword;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * The DomibusConnectorUserPasswordDao interface provides methods to interact with the database
 * table DOMIBUS_CONNECTOR_USER_PWD.
 */
@Repository
public interface DomibusConnectorUserPasswordDao
    extends CrudRepository<PDomibusConnectorUserPassword, Long> {
    @Query(
        "SELECT p FROM PDomibusConnectorUserPassword p WHERE p.user=?1 AND p.currentPassword=true"
    )
    PDomibusConnectorUserPassword findCurrentByUser(PDomibusConnectorUser user);
}
