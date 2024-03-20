
package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorParty;
import java.util.List;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 * @deprecated use DomibusConnectorPModeService instead
 */
@Deprecated
public interface DomibusConnectorPartyPersistenceService {

    public DomibusConnectorParty persistNewParty(DomibusConnectorParty newParty);
    
    public List<DomibusConnectorParty> getPartyList();
    
    public void deleteParty(DomibusConnectorParty party);
    
    public DomibusConnectorParty updateParty(DomibusConnectorParty oldParty, DomibusConnectorParty newParty);
    
    public DomibusConnectorParty getParty(String partyId, String role);

    public DomibusConnectorParty getPartyByPartyId(String partyId);

}
