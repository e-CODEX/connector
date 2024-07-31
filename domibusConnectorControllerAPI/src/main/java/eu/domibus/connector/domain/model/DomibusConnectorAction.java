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
 * The DomibusConnectorAction class represents an action associated with a message in the Domibus
 * system. An action can be performed on a message, such as sending, receiving, or deleting.
 *
 * <p>Instances of this class are used to store the action information of a message in the
 * DomibusConnectorPModeSet class.
 *
 * @author riederb
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class DomibusConnectorAction implements Serializable {
    private Long dbKey;
    private String action;

    /**
     * Constructs a new DomibusConnectorAction with the given action.
     *
     * @param action the action associated with the message
     */
    public DomibusConnectorAction(final String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        var builder = new ToStringCreator(this);
        builder.append("action", this.action);
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final var prime = 31;
        var result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DomibusConnectorAction other = (DomibusConnectorAction) obj;
        if (action == null) {
            if (other.action != null) {
                return false;
            }
        } else if (!action.equals(other.action)) {
            return false;
        }
        return true;
    }
}
