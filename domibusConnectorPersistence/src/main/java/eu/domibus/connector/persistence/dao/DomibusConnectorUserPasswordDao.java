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
