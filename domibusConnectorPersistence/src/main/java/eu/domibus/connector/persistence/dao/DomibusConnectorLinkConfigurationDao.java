/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorLinkConfiguration;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The DomibusConnectorLinkConfigurationDao interface provides methods to interact with the
 * DC_LINK_CONFIGURATION table in the database. It extends the JpaRepository interface for CRUD
 * operations on the PDomibusConnectorLinkConfiguration entity.
 */
@Repository
public interface DomibusConnectorLinkConfigurationDao
    extends JpaRepository<PDomibusConnectorLinkConfiguration, Long> {
    Optional<PDomibusConnectorLinkConfiguration> getOneByConfigName(String configName);
}
