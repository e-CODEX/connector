/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

/**
 * The DomibusConnectorMessageId class represents the connector message ID used in the Domibus
 * connector module. It is a unique identifier for a specific message in the system.
 *
 * <p>This class provides methods to get and set the connector message ID, as well as
 * implementations of the equals, hashCode, and toString methods.
 */
@Validated
public class DomibusConnectorMessageId implements Serializable {
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
        if (this == o) {
            return true;
        }
        if (!(o instanceof DomibusConnectorMessageId that)) {
            return false;
        }

        return connectorMessageId != null ? connectorMessageId.equals(that.connectorMessageId) :
            that.connectorMessageId == null;
    }

    @Override
    public int hashCode() {
        return connectorMessageId != null ? connectorMessageId.hashCode() : 0;
    }

    public String toString() {
        return this.connectorMessageId;
    }
}
