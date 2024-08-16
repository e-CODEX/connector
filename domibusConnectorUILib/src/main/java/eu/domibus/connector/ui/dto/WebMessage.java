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

import java.time.ZonedDateTime;
import java.util.LinkedList;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * The WebMessage class represents a web message that is sent or received by a backend system.
 */
@Data
public class WebMessage {
    private String connectorMessageId;
    private String ebmsMessageId;
    private String backendMessageId;
    private String conversationId;
    private String backendName;
    private String directionSource;
    private String directionTarget;
    private ZonedDateTime deliveredToNationalSystem;
    private ZonedDateTime deliveredToGateway;
    private ZonedDateTime created;
    private ZonedDateTime confirmed;
    private ZonedDateTime rejected;
    private WebMessageDetail messageInfo = new WebMessageDetail();
    private LinkedList<WebMessageEvidence> evidences = new LinkedList<>();
    private LinkedList<WebMessageFile> files = new LinkedList<>();

    public String getConfirmedString() {
        return confirmed != null ? confirmed.toString() : null;
    }

    public String getDeliveredToNationalSystemString() {
        return deliveredToNationalSystem != null ? deliveredToNationalSystem.toString() : null;
    }

    public String getDeliveredToGatewayString() {
        return deliveredToGateway != null ? deliveredToGateway.toString() : null;
    }

    public String getCreatedString() {
        return created != null ? created.toString() : null;
    }

    public String getRejectedString() {
        return rejected != null ? rejected.toString() : null;
    }

    @Override
    public String toString() {
        return "WebMessage [connectorMessageId=" + connectorMessageId + ", ebmsMessageId="
            + ebmsMessageId
            + ", backendMessageId=" + backendMessageId + ", created=" + created + ", messageInfo="
            + messageInfo
            + "]";
    }

    /**
     * Returns the direction of the web message.
     *
     * <p>The direction is determined by the values of the directionSource and directionTarget
     * properties. If both properties are not empty, the direction is formed by concatenating the
     * directionSource and directionTarget values with " TO " in between. If either of the
     * properties is empty, an empty string is returned.
     *
     * @return the direction of the web message as a string. If either directionSource or
     *      directionTarget is empty, returns an empty string.
     */
    public String getDirection() {
        if (!StringUtils.isEmpty(getDirectionSource()) && !StringUtils.isEmpty(
            getDirectionTarget())) {
            return getDirectionSource() + " TO " + getDirectionTarget();
        }
        return "";
    }
}
