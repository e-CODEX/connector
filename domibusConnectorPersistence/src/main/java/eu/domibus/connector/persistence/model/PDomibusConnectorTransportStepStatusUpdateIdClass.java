package eu.domibus.connector.persistence.model;

import java.io.Serializable;
import java.util.Objects;


public class PDomibusConnectorTransportStepStatusUpdateIdClass implements Serializable {
    Long transportStep;
    String transportStateString;

    public Long getTransportStep() {
        return transportStep;
    }

    public void setTransportStep(Long transportStep) {
        this.transportStep = transportStep;
    }

    public String getTransportStateString() {
        return transportStateString;
    }

    public void setTransportStateString(String transportStateTransportState) {
        this.transportStateString = transportStateTransportState;
    }
    @Override
    public int hashCode() {
        int result = transportStep != null ? transportStep.hashCode() : 0;
        result = 31 * result + (transportStateString != null ? transportStateString.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PDomibusConnectorTransportStepStatusUpdateIdClass)) return false;

        PDomibusConnectorTransportStepStatusUpdateIdClass that = (PDomibusConnectorTransportStepStatusUpdateIdClass) o;

        if (!Objects.equals(transportStep, that.transportStep))
            return false;
        return Objects.equals(transportStateString, that.transportStateString);
    }
}
