/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * The PDomibusConnectorTransportStepStatusUpdateIdClass represents the ID of a status update for a
 * transport step in Domibus connector. It is used as the composite primary key for the
 * PDomibusConnectorTransportStepStatusUpdate entity.
 */
@Getter
@Setter
public class PDomibusConnectorTransportStepStatusUpdateIdClass implements Serializable {
    Long transportStep;
    String transportStateString;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PDomibusConnectorTransportStepStatusUpdateIdClass that)) {
            return false;
        }

        if (transportStep != null ? !transportStep.equals(that.transportStep) :
            that.transportStep != null) {
            return false;
        }
        return transportStateString != null
            ? transportStateString.equals(that.transportStateString) :
            that.transportStateString == null;
    }

    @Override
    public int hashCode() {
        int result = transportStep != null ? transportStep.hashCode() : 0;
        result = 31 * result + (transportStateString != null ? transportStateString.hashCode() : 0);
        return result;
    }
}
