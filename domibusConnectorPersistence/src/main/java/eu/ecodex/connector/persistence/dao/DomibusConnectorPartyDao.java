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

import eu.ecodex.connector.persistence.model.PDomibusConnectorParty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the data access object (DAO) for the DomibusConnectorParty entity. It
 * extends the CrudRepository interface, providing basic CRUD operations for the
 * DomibusConnectorParty entity.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorPartyDao extends CrudRepository<PDomibusConnectorParty, Long> {
}
