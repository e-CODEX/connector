/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model;

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
