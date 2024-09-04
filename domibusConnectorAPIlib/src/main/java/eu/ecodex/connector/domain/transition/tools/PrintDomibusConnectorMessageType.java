/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.transition.tools;

import eu.ecodex.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.domain.transition.DomibusConnectorPartyType;
import eu.ecodex.connector.domain.transition.DomibusConnectorServiceType;
import lombok.experimental.UtilityClass;
import org.springframework.core.style.ToStringCreator;

/**
 * Utility class for printing DomibusConnectorMessageType objects.
 */
@UtilityClass
public class PrintDomibusConnectorMessageType {
    private static final String SERVICE_FIELD_NAME = "service";

    /**
     * Helper method to print MessageType without DataAttachments.
     *
     * @param messageType the message
     * @return the converted message
     */
    public static String messageToString(DomibusConnectorMessageType messageType) {
        DomibusConnectorMessageDetailsType messageDetails = messageType.getMessageDetails();
        var messageDetailsToString = new ToStringCreator(messageDetails);

        messageDetailsToString.append("backendMessageId", messageDetails.getBackendMessageId());
        messageDetailsToString.append("ebmsMessageId", messageDetails.getEbmsMessageId());
        messageDetailsToString.append("refToMessageId", messageDetails.getRefToMessageId());
        messageDetailsToString.append("conversationId", messageDetails.getConversationId());
        messageDetailsToString.append("originalSender", messageDetails.getOriginalSender());
        messageDetailsToString.append("finalRecipient", messageDetails.getFinalRecipient());

        //append service
        if (messageDetails.getService() != null) {
            DomibusConnectorServiceType service = messageDetails.getService();
            var serviceToStringCreator = new ToStringCreator(service);
            serviceToStringCreator.append(SERVICE_FIELD_NAME, service.getService());
            serviceToStringCreator.append("serviceType", service.getServiceType());
            messageDetailsToString.append(SERVICE_FIELD_NAME, serviceToStringCreator.toString());
        } else {
            messageDetailsToString.append(SERVICE_FIELD_NAME, null);
        }
        //append action
        messageDetailsToString.append("action", messageDetails.getAction());
        //append from party
        messageDetailsToString.append("fromParty", partyToString(messageDetails.getFromParty()));
        //append to party
        messageDetailsToString.append("toParty", partyToString(messageDetails.getToParty()));

        // TODO: append content
        // TODO: append attachments
        // TODO: append confirmations
        // TODO: append errors

        var messageToString = new ToStringCreator(messageType);
        messageToString.append("messageDetails", messageDetailsToString.toString());
        return messageToString.toString();
    }

    private static String partyToString(DomibusConnectorPartyType party) {
        var partyToStringCreator = new ToStringCreator(party);
        partyToStringCreator.append("partyId", party.getPartyId());
        partyToStringCreator.append("partyIdType", party.getPartyIdType());
        partyToStringCreator.append("role", party.getRole());
        return partyToStringCreator.toString();
    }
}
