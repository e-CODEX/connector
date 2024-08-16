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
