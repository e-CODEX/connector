/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service;

import eu.ecodex.connector.domain.model.DomibusConnectorParty;
import java.util.List;

/**
 * The DomibusConnectorPartyPersistenceService interface provides methods for managing parties in
 * the Domibus connector. It allows the creation, retrieval, update, and deletion of parties.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 * @deprecated use DomibusConnectorPModeService instead
 */
@Deprecated
public interface DomibusConnectorPartyPersistenceService {
    DomibusConnectorParty persistNewParty(DomibusConnectorParty newParty);

    List<DomibusConnectorParty> getPartyList();

    void deleteParty(DomibusConnectorParty party);

    DomibusConnectorParty updateParty(DomibusConnectorParty oldParty,
                                      DomibusConnectorParty newParty);

    DomibusConnectorParty getParty(String partyId, String role);

    DomibusConnectorParty getPartyByPartyId(String partyId);
}
