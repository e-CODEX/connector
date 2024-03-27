package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.model.DomibusConnectorService;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public final class DomibusConnectorServiceBuilder {
    private String service;
    private String serviceType;

    private DomibusConnectorServiceBuilder() {
    }

    public static DomibusConnectorServiceBuilder createBuilder() {
        return new DomibusConnectorServiceBuilder();
    }

    public DomibusConnectorServiceBuilder setService(String service) {
        this.service = service;
        return this;
    }

    public DomibusConnectorServiceBuilder withServiceType(String serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    public DomibusConnectorService build() {
        if (service == null) {
            throw new IllegalArgumentException("Service must be set!");
        }
        return new DomibusConnectorService(service, serviceType);
    }

    public DomibusConnectorServiceBuilder copyPropertiesFrom(DomibusConnectorService service) {
        this.service = service.getService();
        this.serviceType = service.getServiceType();
        return this;
    }
}
