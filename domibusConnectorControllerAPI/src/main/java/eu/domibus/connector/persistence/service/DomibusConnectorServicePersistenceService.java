
package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorService;
import java.util.List;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 * @deprecated use DomibusConnectorPModeService instead!
 */
@Deprecated
public interface DomibusConnectorServicePersistenceService {

    public DomibusConnectorService persistNewService(DomibusConnectorService newService);
    
    public List<DomibusConnectorService> getServiceList();
    
    public DomibusConnectorService updateService(DomibusConnectorService oldService, DomibusConnectorService newService);
    
    public void deleteService(DomibusConnectorService service);

    public DomibusConnectorService getService(String service);

	List<String> getServiceListString();

}
