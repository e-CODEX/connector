/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.model.DomibusConnectorService;

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
