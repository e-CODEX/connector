package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorParty;

import java.util.List;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 * @deprecated use DomibusConnectorPModeService instead
 */
@Deprecated
public interface DomibusConnectorPartyPersistenceService {
    DomibusConnectorParty persistNewParty(DomibusConnectorParty newParty);

    List<DomibusConnectorParty> getPartyList();

    void deleteParty(DomibusConnectorParty party);

    DomibusConnectorParty updateParty(DomibusConnectorParty oldParty, DomibusConnectorParty newParty);

    DomibusConnectorParty getParty(String partyId, String role);

    DomibusConnectorParty getPartyByPartyId(String partyId);
}
