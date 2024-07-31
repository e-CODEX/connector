/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import java.util.List;
import java.util.Optional;

/**
 * The DCBusinessDomainPersistenceService interface provides methods for accessing and manipulating
 * business domain data in Domibus.
 */
public interface DCBusinessDomainPersistenceService {
    Optional<DomibusConnectorBusinessDomain> findById(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId);

    List<DomibusConnectorBusinessDomain> findAll();

    DomibusConnectorBusinessDomain update(
        DomibusConnectorBusinessDomain domibusConnectorBusinessDomain);

    DomibusConnectorBusinessDomain create(DomibusConnectorBusinessDomain businessDomain);
}
