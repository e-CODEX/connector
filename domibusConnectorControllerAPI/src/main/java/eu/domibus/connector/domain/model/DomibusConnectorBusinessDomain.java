package eu.domibus.connector.domain.model;

import eu.domibus.connector.domain.enums.ConfigurationSource;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

public class DomibusConnectorBusinessDomain {

    public static final String DEFAULT_LANE_NAME = "defaultBusinessDomain";

    private BusinessDomainId id;

    private String description;

    private boolean enabled;

    private Map<String, String> messageLaneProperties = new HashMap<>();

    private ConfigurationSource configurationSource;

    public static DomibusConnectorBusinessDomain getDefaultMessageLane() {
        DomibusConnectorBusinessDomain defaultMessageLane = new DomibusConnectorBusinessDomain();
        defaultMessageLane.setId(new BusinessDomainId(DEFAULT_LANE_NAME));
        defaultMessageLane.setDescription("default message lane");
        defaultMessageLane.setMessageLaneProperties(new HashMap<>());
        return defaultMessageLane;
    }

    public static BusinessDomainId getDefaultMessageLaneId() {
        return getDefaultMessageLane().getId();
    }

    public BusinessDomainId getId() {
        return id;
    }

    public void setId(BusinessDomainId id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getMessageLaneProperties() {
        return messageLaneProperties;
    }

    public void setMessageLaneProperties(Map<String, String> messageLaneProperties) {
        this.messageLaneProperties = messageLaneProperties;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ConfigurationSource getConfigurationSource() {
        return configurationSource;
    }

    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomibusConnectorBusinessDomain)) return false;

        DomibusConnectorBusinessDomain that = (DomibusConnectorBusinessDomain) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static class BusinessDomainId {

        public BusinessDomainId() {}

        public BusinessDomainId(String id) {
            this.messageLaneId = id;
        }

        @NotBlank
        private String messageLaneId;

        public String getMessageLaneId() {
            return messageLaneId;
        }

        public void setMessageLaneId(String messageLaneId) {
            this.messageLaneId = messageLaneId;
        }

        @Override
        public String toString() {
            return String.format("MessageLaneId: [%s]", this.messageLaneId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BusinessDomainId)) return false;

            BusinessDomainId that = (BusinessDomainId) o;

            return messageLaneId != null ? messageLaneId.equals(that.messageLaneId) : that.messageLaneId == null;
        }

        @Override
        public int hashCode() {
            return messageLaneId != null ? messageLaneId.hashCode() : 0;
        }
    }

}
