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
