/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
