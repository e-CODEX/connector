/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
