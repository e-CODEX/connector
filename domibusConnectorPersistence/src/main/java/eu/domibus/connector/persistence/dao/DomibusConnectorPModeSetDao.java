/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.persistence.model.PDomibusConnectorPModeSet;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * The DomibusConnectorPModeSetDao interface provides CRUD operations for the Domibus Connector
 * PMode Set entity. It extends the CrudRepository interface and specifies additional methods for
 * querying the database based on specific criteria.
 */
@Repository
public interface DomibusConnectorPModeSetDao
    extends CrudRepository<PDomibusConnectorPModeSet, Long> {
    @Query(
        "SELECT p FROM PDomibusConnectorPModeSet p "
            + "WHERE p.active = true AND p.messageLane.name=?1 ORDER by p.created"
    )
    List<PDomibusConnectorPModeSet> getCurrentActivePModeSet(
        DomibusConnectorBusinessDomain.BusinessDomainId id);

    @Query(
        "SELECT p FROM PDomibusConnectorPModeSet p "
            + "WHERE p.active = false AND p.messageLane.name=?1 ORDER by p.created desc"
    )
    List<PDomibusConnectorPModeSet> getInactivePModeSets(
        DomibusConnectorBusinessDomain.BusinessDomainId id);
}
