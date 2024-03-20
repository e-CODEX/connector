package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorService;
import eu.domibus.connector.persistence.model.PDomibusConnectorService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Nullable;

public class ServiceMapper {

    static @Nullable
	public
    DomibusConnectorService mapServiceToDomain(@Nullable PDomibusConnectorService persistenceService) {
        if (persistenceService != null) {
            eu.domibus.connector.domain.model.DomibusConnectorService service
                    = new eu.domibus.connector.domain.model.DomibusConnectorService(
                    persistenceService.getService(),
                    persistenceService.getServiceType()
            );
            service.setDbKey(persistenceService.getId());
            return service;
        }
        return null;
    }

    static @Nullable
	public
    PDomibusConnectorService mapServiceToPersistence(@Nullable DomibusConnectorService service) {
        if (service != null) {
            PDomibusConnectorService persistenceService = new PDomibusConnectorService();
            persistenceService.setServiceType(service.getServiceType());
            persistenceService.setService(service.getService());
            persistenceService.setId(service.getDbKey());
            return persistenceService;
        }
        return null;
    }

}
