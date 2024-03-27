package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.model.DomibusConnectorService;


public class DomibusConnectorMessageDetailsBuilder {
    private String refToBackendMessageId;
    private String backendMessageId;
    private String ebmsMessageId;
    private String refToMessageId;
    private String conversationId;
    private String originalSender;
    private String finalRecipient;
    private DomibusConnectorService service;
    private DomibusConnectorAction action;
    private DomibusConnectorParty fromParty;
    private DomibusConnectorParty toParty;
    private DomibusConnectorMessageDirection messageDirection;

    private String connectorBackendClientName;
    private String gatewayName;

    private DomibusConnectorMessageDetailsBuilder() {
    }

    public static DomibusConnectorMessageDetailsBuilder create() {
        return new DomibusConnectorMessageDetailsBuilder();
    }

    public DomibusConnectorMessageDetailsBuilder withBackendMessageId(String backendMessageId) {
        this.backendMessageId = backendMessageId;
        return this;
    }

    public DomibusConnectorMessageDetailsBuilder withEbmsMessageId(String ebmsMessageId) {
        this.ebmsMessageId = ebmsMessageId;
        return this;
    }

    public DomibusConnectorMessageDetailsBuilder withRefToMessageId(String refToMessageId) {
        this.refToMessageId = refToMessageId;
        return this;
    }

    public DomibusConnectorMessageDetailsBuilder withConnectorBackendClientName(String connectorBackendClientName) {
        this.connectorBackendClientName = connectorBackendClientName;
        return this;
    }

    public DomibusConnectorMessageDetailsBuilder withGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
        return this;
    }

    public DomibusConnectorMessageDetailsBuilder withConversationId(String conversationId) {
        this.conversationId = conversationId;
        return this;
    }

    public DomibusConnectorMessageDetailsBuilder withOriginalSender(String originalSender) {
        this.originalSender = originalSender;
        return this;
    }

    public DomibusConnectorMessageDetailsBuilder withFinalRecipient(String finalRecipient) {
        this.finalRecipient = finalRecipient;
        return this;
    }

    public DomibusConnectorMessageDetailsBuilder withService(String serviceName, String serviceType) {
        this.service = new DomibusConnectorService(serviceName, serviceType);
        return this;
    }

    public DomibusConnectorMessageDetailsBuilder withService(DomibusConnectorService service) {
        this.service = service;
        return this;
    }

    public DomibusConnectorMessageDetailsBuilder withAction(String action) {
        this.action = new DomibusConnectorAction(action);
        // this.action = new DomibusConnectorAction(action, true);
        return this;
    }

    public DomibusConnectorMessageDetailsBuilder withAction(DomibusConnectorAction action) {
        this.action = action;
        return this;
    }

    public DomibusConnectorMessageDetailsBuilder withFromParty(DomibusConnectorParty fromParty) {
        this.fromParty = fromParty;
        return this;
    }

    public DomibusConnectorMessageDetailsBuilder withToParty(DomibusConnectorParty toParty) {
        this.toParty = toParty;
        return this;
    }

    public DomibusConnectorMessageDetails build() {
        DomibusConnectorMessageDetails details = new DomibusConnectorMessageDetails();
        details.setAction(this.action);
        details.setService(this.service);
        details.setConversationId(this.conversationId);
        details.setEbmsMessageId(this.ebmsMessageId);
        details.setFinalRecipient(this.finalRecipient);
        details.setBackendMessageId(this.backendMessageId);
        details.setOriginalSender(this.originalSender);
        details.setFromParty(this.fromParty);
        details.setToParty(this.toParty);
        details.setRefToMessageId(this.refToMessageId);
        details.setRefToBackendMessageId(this.refToBackendMessageId);
        details.setDirection(this.messageDirection);
        details.setConnectorBackendClientName(this.connectorBackendClientName);
        details.setGatewayName(this.gatewayName);
        return details;
    }

    public DomibusConnectorMessageDetailsBuilder copyPropertiesFrom(DomibusConnectorMessageDetails messageDetails) {
        if (messageDetails.getAction() != null) {
            this.action = DomibusConnectorActionBuilder
                    .createBuilder()
                    .copyPropertiesFrom(messageDetails.getAction())
                    .build();
        }
        if (messageDetails.getService() != null) {
            this.service = DomibusConnectorServiceBuilder
                    .createBuilder()
                    .copyPropertiesFrom(messageDetails.getService())
                    .build();
        }
        this.conversationId = messageDetails.getConversationId();
        this.ebmsMessageId = messageDetails.getEbmsMessageId();
        this.finalRecipient = messageDetails.getFinalRecipient();
        this.originalSender = messageDetails.getOriginalSender();
        if (messageDetails.getFromParty() != null) {
            this.fromParty = DomibusConnectorPartyBuilder
                    .createBuilder()
                    .copyPropertiesFrom(messageDetails.getFromParty())
                    .build();
        }
        if (messageDetails.getToParty() != null) {
            this.toParty = DomibusConnectorPartyBuilder
                    .createBuilder()
                    .copyPropertiesFrom(messageDetails.getToParty())
                    .build();
        }
        this.refToMessageId = messageDetails.getRefToMessageId();
        this.refToBackendMessageId = messageDetails.getRefToBackendMessageId();
        this.messageDirection = messageDetails.getDirection();
        this.connectorBackendClientName = messageDetails.getConnectorBackendClientName();
        this.gatewayName = messageDetails.getGatewayName();

        return this;
    }
}
