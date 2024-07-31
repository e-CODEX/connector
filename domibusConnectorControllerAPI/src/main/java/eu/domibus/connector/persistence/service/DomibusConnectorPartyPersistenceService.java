/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorParty;
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
