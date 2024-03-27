package eu.domibus.connector.domain.transition.tools;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.*;
import org.springframework.core.style.ToStringCreator;


public class PrintDomibusConnectorMessageType {
    /**
     * Helper method to print MessageType without DataAttachments
     *
     * @param messageType the message
     * @return the converted message
     */
    public static String messageToString(DomibusConnectorMessageType messageType) {
        DomibusConnectorMessageDetailsType messageDetails = messageType.getMessageDetails();
        ToStringCreator messageDetailsToString = new ToStringCreator(messageDetails);

        messageDetailsToString.append("backendMessageId", messageDetails.getBackendMessageId());
        messageDetailsToString.append("ebmsMessageId", messageDetails.getEbmsMessageId());
        messageDetailsToString.append("refToMessageId", messageDetails.getRefToMessageId());
        messageDetailsToString.append("conversationId", messageDetails.getConversationId());
        messageDetailsToString.append("originalSender", messageDetails.getOriginalSender());
        messageDetailsToString.append("finalRecipient", messageDetails.getFinalRecipient());

        // append service
        if (messageDetails.getService() != null) {
            DomibusConnectorServiceType service = messageDetails.getService();
            ToStringCreator serviceToStringCreator = new ToStringCreator(service);
            serviceToStringCreator.append("service", service.getService());
            serviceToStringCreator.append("serviceType", service.getServiceType());
            messageDetailsToString.append("service", serviceToStringCreator.toString());
        } else {
            messageDetailsToString.append("service", null);
        }
        // append action
        messageDetailsToString.append("action", messageDetails.getAction());
        // append from party
        messageDetailsToString.append("fromParty", partyToString(messageDetails.getFromParty()));
        // append to party
        messageDetailsToString.append("toParty", partyToString(messageDetails.getToParty()));

        // TODO: append content
        // TODO: append attachments
        // TODO: append confirmations
        // TODO: append errors

        ToStringCreator messageToString = new ToStringCreator(messageType);
        messageToString.append("messageDetails", messageDetailsToString.toString());

        return messageToString.toString();
    }

    private static String partyToString(DomibusConnectorPartyType party) {
        ToStringCreator partyToStringCreator = new ToStringCreator(party);
        partyToStringCreator.append("partyId", party.getPartyId());
        partyToStringCreator.append("partyIdType", party.getPartyIdType());
        partyToStringCreator.append("role", party.getRole());

        return partyToStringCreator.toString();
    }
}
