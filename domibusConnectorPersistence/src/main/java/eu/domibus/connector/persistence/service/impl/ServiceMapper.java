/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorService;
import eu.domibus.connector.persistence.model.PDomibusConnectorService;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;

/**
 * The ServiceMapper class provides static methods to map between the DomibusConnectorService domain
 * class and the PDomibusConnectorService persistence class.
 */
@UtilityClass
public class ServiceMapper {
    /**
     * Maps a persistence service object to a domain service object.
     *
     * @param persistenceService the persistence service object to be mapped. Can be null.
     * @return the mapped domain service object. Returns null if the input persistence service is
     *      null.
     */
    static @Nullable
    public DomibusConnectorService mapServiceToDomain(
        @Nullable PDomibusConnectorService persistenceService) {
        if (persistenceService != null) {
            var service = new eu.domibus.connector.domain.model.DomibusConnectorService(
                persistenceService.getService(),
                persistenceService.getServiceType()
            );
            service.setDbKey(persistenceService.getId());
            return service;
        }
        return null;
    }

    /**
     * Maps a DomibusConnectorService object to a PDomibusConnectorService object.
     *
     * @param service the DomibusConnectorService object to be mapped. Can be null.
     * @return the mapped PDomibusConnectorService object. Returns null if the input service is
     *      null.
     */
    static @Nullable
    public PDomibusConnectorService mapServiceToPersistence(
        @Nullable DomibusConnectorService service) {
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
