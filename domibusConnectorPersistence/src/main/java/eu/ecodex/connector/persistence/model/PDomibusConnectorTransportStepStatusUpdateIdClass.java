/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.model;

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
