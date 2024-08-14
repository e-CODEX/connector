/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.dto;

import java.util.Date;
import lombok.Data;

/**
 * The WebMessageEvidence class represents evidence related to a web message in a backend system.
 */
@SuppressWarnings("squid:S1135")
@Data
public class WebMessageEvidence {
    private String evidenceType;
    private Date deliveredToGateway;
    private Date deliveredToBackend;

    public String getDeliveredToGatewayString() {
        return deliveredToGateway != null ? deliveredToGateway.toString() : null;
    }

    public void setDeliveredToGatewayString(String deliveredToGateway) {
        // TODO see why this method body is empty
    }

    public String getDeliveredToBackendString() {
        return deliveredToBackend != null ? deliveredToBackend.toString() : null;
    }

    public void setDeliveredToBackendString(String deliveredToBackend) {
        // TODO see why this method body is empty
    }
}
