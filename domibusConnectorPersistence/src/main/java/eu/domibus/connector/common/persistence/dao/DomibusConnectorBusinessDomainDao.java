/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.common.persistence.dao;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessageLane;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The DomibusConnectorBusinessDomainDao interface provides data access methods for managing Domibus
 * Connector message lanes.
 */
@Repository
public interface DomibusConnectorBusinessDomainDao
    extends JpaRepository<PDomibusConnectorMessageLane, Long> {
    Optional<PDomibusConnectorMessageLane> findByName(
        DomibusConnectorBusinessDomain.BusinessDomainId name);
}
