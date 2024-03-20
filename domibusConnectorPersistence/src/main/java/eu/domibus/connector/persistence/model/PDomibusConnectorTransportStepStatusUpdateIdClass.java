package eu.domibus.connector.persistence.model;

import eu.domibus.connector.domain.enums.TransportState;

import java.io.Serializable;


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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PDomibusConnectorTransportStepStatusUpdateIdClass)) return false;

        PDomibusConnectorTransportStepStatusUpdateIdClass that = (PDomibusConnectorTransportStepStatusUpdateIdClass) o;

        if (transportStep != null ? !transportStep.equals(that.transportStep) : that.transportStep != null)
            return false;
        return transportStateString != null ? transportStateString.equals(that.transportStateString) : that.transportStateString == null;
    }

    @Override
    public int hashCode() {
        int result = transportStep != null ? transportStep.hashCode() : 0;
        result = 31 * result + (transportStateString != null ? transportStateString.hashCode() : 0);
        return result;
    }
}
