/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service.impl;

import eu.ecodex.connector.domain.model.DomibusConnectorService;
import eu.ecodex.connector.persistence.model.PDomibusConnectorService;
import jakarta.annotation.Nullable;
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
            var service = new DomibusConnectorService(
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
