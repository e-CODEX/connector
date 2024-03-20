package eu.domibus.connector.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
public class DomibusConnectorMessageId {

    @NotBlank
    String connectorMessageId;

    public DomibusConnectorMessageId() {
    }

    public DomibusConnectorMessageId(String connectorMessageId) {
        this.connectorMessageId = connectorMessageId;
    }

    @JsonProperty(required = true)
    public String getConnectorMessageId() {
        return connectorMessageId;
    }

    @JsonProperty(required = true)
    public void setConnectorMessageId(String connectorMessageId) {
        this.connectorMessageId = connectorMessageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomibusConnectorMessageId)) return false;

        DomibusConnectorMessageId that = (DomibusConnectorMessageId) o;

        return connectorMessageId != null ? connectorMessageId.equals(that.connectorMessageId) : that.connectorMessageId == null;
    }

    @Override
    public int hashCode() {
        return connectorMessageId != null ? connectorMessageId.hashCode() : 0;
    }

    public String toString() {
        return this.connectorMessageId;
    }
}
