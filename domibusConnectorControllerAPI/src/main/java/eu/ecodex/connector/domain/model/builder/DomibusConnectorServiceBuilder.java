/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.builder;

import eu.ecodex.connector.domain.model.DomibusConnectorService;

/**
 * The DomibusConnectorServiceBuilder class is used to build instances of DomibusConnectorService.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public final class DomibusConnectorServiceBuilder {
    private String service;
    private String serviceType;

    public static DomibusConnectorServiceBuilder createBuilder() {
        return new DomibusConnectorServiceBuilder();
    }

    private DomibusConnectorServiceBuilder() {
    }

    public DomibusConnectorServiceBuilder setService(String service) {
        this.service = service;
        return this;
    }

    public DomibusConnectorServiceBuilder withServiceType(String serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    /**
     * Builds a new instance of DomibusConnectorService using the provided service and serviceType.
     *
     * @return the newly created DomibusConnectorService object
     * @throws IllegalArgumentException if the service is not set
     */
    public DomibusConnectorService build() {
        if (service == null) {
            throw new IllegalArgumentException("Service must be set!");
        }
        return new DomibusConnectorService(service, serviceType);
    }

    /**
     * Copies the properties from the given DomibusConnectorService to the current
     * DomibusConnectorServiceBuilder instance.
     *
     * @param service the DomibusConnectorService to copy properties from
     * @return the current DomibusConnectorServiceBuilder instance
     * @throws IllegalArgumentException if the service is null
     */
    public DomibusConnectorServiceBuilder copyPropertiesFrom(DomibusConnectorService service) {
        this.service = service.getService();
        this.serviceType = service.getServiceType();
        return this;
    }
}
