/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.style.ToStringCreator;

/**
 * The DomibusConnectorService class represents a service provided by the Domibus connector.
 *
 * @author riederb
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class DomibusConnectorService implements Serializable {
    private Long dbKey;
    private String service;
    private String serviceType;

    /**
     * Creates a new instance of DomibusConnectorService with specified service and serviceType.
     *
     * @param service     the service provided by the Domibus connector
     * @param serviceType the type of the service provided by the Domibus connector
     */
    public DomibusConnectorService(final String service, final String serviceType) {
        this.service = service;
        this.serviceType = serviceType;
    }

    @Override
    public String toString() {
        var builder = new ToStringCreator(this);
        builder.append("service", this.service);
        builder.append("serviceType", this.serviceType);
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DomibusConnectorService that = (DomibusConnectorService) o;

        return service != null ? service.equals(that.service) : that.service == null;
    }

    @Override
    public int hashCode() {
        return service != null ? service.hashCode() : 0;
    }
}
