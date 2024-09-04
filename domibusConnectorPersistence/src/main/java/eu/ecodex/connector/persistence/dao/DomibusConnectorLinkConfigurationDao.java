/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.dao;

import eu.ecodex.connector.persistence.model.PDomibusConnectorLinkConfiguration;
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
