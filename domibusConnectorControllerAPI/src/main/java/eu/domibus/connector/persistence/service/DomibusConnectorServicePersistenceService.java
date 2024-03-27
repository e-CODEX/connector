package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorService;

import java.util.List;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 * @deprecated use DomibusConnectorPModeService instead!
 */
@Deprecated
public interface DomibusConnectorServicePersistenceService {
    DomibusConnectorService persistNewService(DomibusConnectorService newService);

    List<DomibusConnectorService> getServiceList();

    DomibusConnectorService updateService(DomibusConnectorService oldService, DomibusConnectorService newService);

    void deleteService(DomibusConnectorService service);

    DomibusConnectorService getService(String service);

    List<String> getServiceListString();
}
