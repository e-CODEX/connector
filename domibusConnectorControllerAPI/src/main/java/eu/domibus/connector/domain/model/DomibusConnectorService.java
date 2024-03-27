package eu.domibus.connector.domain.model;

import org.springframework.core.style.ToStringCreator;

import java.io.Serializable;


/**
 * @author riederb
 * @version 1.0
 */
public class DomibusConnectorService implements Serializable {
    private Long dbKey;
    private String service;
    private String serviceType;

    /**
     * Default constructor, needed for frameworks
     * to serialize and deserialize objects of this class
     */
    public DomibusConnectorService() {
    }

    /**
     * @param service     service
     * @param serviceType serviceType
     */
    public DomibusConnectorService(final String service, final String serviceType) {
        this.service = service;
        this.serviceType = serviceType;
    }

    public String getService() {
        return this.service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getServiceType() {
        return this.serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Long getDbKey() {
        return dbKey;
    }

    public void setDbKey(Long dbKey) {
        this.dbKey = dbKey;
    }

    @Override
    public int hashCode() {
        return service != null ? service.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomibusConnectorService that = (DomibusConnectorService) o;

        return service != null ? service.equals(that.service) : that.service == null;
    }

    @Override
    public String toString() {
        ToStringCreator builder = new ToStringCreator(this);
        builder.append("service", this.service);
        builder.append("serviceType", this.serviceType);
        return builder.toString();
    }
}
