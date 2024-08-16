/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
