package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.common.service.ConfigurationPropertyManagerService;
import eu.domibus.connector.controller.processor.util.ConfirmationCreatorService;
import eu.domibus.connector.controller.service.DomibusConnectorMessageIdGenerator;
import eu.domibus.connector.controller.spring.ConnectorMessageProcessingProperties;
import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.model.*;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageDetailsBuilder;
import eu.domibus.connector.lib.logging.MDC;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;


/**
 * This step sends a by the connector generated evidence
 * <p>
 * <p>
 * For this purpose a new DomibusConnectorMessage
 * with a new DomibusConnectorMessageId is created
 * this message is not stored into the DB, it is only
 * used within the Queues
 */
@Component
public class SubmitConfirmationAsEvidenceMessageStep {
    private static final Logger LOGGER = LogManager.getLogger(SubmitConfirmationAsEvidenceMessageStep.class);

    private final SubmitMessageToLinkModuleQueueStep submitMessageToLinkModuleQueueStep;
    private final ConfigurationPropertyManagerService configurationPropertyLoaderService;
    private final ConfirmationCreatorService confirmationCreator;
    private final DomibusConnectorMessageIdGenerator messageIdGenerator;

    public SubmitConfirmationAsEvidenceMessageStep(
            SubmitMessageToLinkModuleQueueStep submitMessageToLinkModuleQueueStep,
            ConfigurationPropertyManagerService configurationPropertyLoaderService,
            ConfirmationCreatorService confirmationCreator,
            DomibusConnectorMessageIdGenerator messageIdGenerator) {
        this.submitMessageToLinkModuleQueueStep = submitMessageToLinkModuleQueueStep;
        this.configurationPropertyLoaderService = configurationPropertyLoaderService;
        this.confirmationCreator = confirmationCreator;
        this.messageIdGenerator = messageIdGenerator;
    }

    /**
     * sends the supplied confirmation as
     * evidence message in the same direction
     * as the supplied business message
     * <p>
     * for this purpose a new message is generated
     * this message is NOT stored into the DB
     *
     * @param businessMessage the business message
     * @param confirmation    the confirmation
     */
    @MDC(
            name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME,
            value = "SubmitConfirmationAsEvidenceMessageStep#sameDirection"
    )
    public void submitSameDirection(
            DomibusConnectorMessageId messageId,
            DomibusConnectorMessage businessMessage,
            DomibusConnectorMessageConfirmation confirmation) {
        validateParameters(businessMessage);
        DomibusConnectorMessage evidenceMessage = buildEvidenceMessage(messageId, businessMessage, confirmation);
        submitMessageToLinkModuleQueueStep.submitMessage(evidenceMessage);
    }

    /**
     * sends the supplied confirmation as
     * evidence message in the opposite direction
     * as the supplied business message
     * <p>
     * for this purpose a new message is generated
     * this message is NOT stored into the DB
     *
     * @param messageId       the connector message id of the new confirmation message
     * @param businessMessage the business message
     * @param confirmation    the confirmation
     */
    @MDC(
            name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME,
            value = "SubmitConfirmationAsEvidenceMessageStep#oppositeDirection"
    )
    public void submitOppositeDirection(
            DomibusConnectorMessageId messageId,
            DomibusConnectorMessage businessMessage,
            DomibusConnectorMessageConfirmation confirmation) {
        validateParameters(businessMessage);
        // DomibusConnectorMessageDirection revertedDirection = DomibusConnectorMessageDirection.revert
        // (businessMessage.getMessageDetails().getDirection());
        DomibusConnectorMessage evidenceMessage = buildEvidenceMessage(messageId, businessMessage, confirmation);
        submitMessageToLinkModuleQueueStep.submitMessageOpposite(businessMessage, evidenceMessage);
    }

    private DomibusConnectorMessage buildEvidenceMessage(
            DomibusConnectorMessageId messageId,
            DomibusConnectorMessage businessMessage,
            DomibusConnectorMessageConfirmation confirmation) {
        if (messageId == null) {
            messageId = messageIdGenerator.generateDomibusConnectorMessageId();
            LOGGER.info("MessageId is null starting new message transport.");
        }
        try (
                final CloseableThreadContext.Instance ctc =
                        CloseableThreadContext.put(
                                LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME,
                                messageId.getConnectorMessageId()
                        )
        ) {

            DomibusConnectorEvidenceType evidenceType = confirmation.getEvidenceType();
            DomibusConnectorAction evidenceAction = confirmationCreator.createEvidenceAction(evidenceType);

            DomibusConnectorMessageDetails messageDetails = DomibusConnectorMessageDetailsBuilder
                    .create()
                    .copyPropertiesFrom(
                            businessMessage.getMessageDetails())
                    .withAction(
                            evidenceAction)
                    .build();
            messageDetails.setRefToMessageId(businessMessage.getMessageDetails().getEbmsMessageId());
            messageDetails.setRefToBackendMessageId(businessMessage.getMessageDetails().getBackendMessageId());

            DomibusConnectorMessage evidenceMessage = DomibusConnectorMessageBuilder
                    .createBuilder()
                    .addTransportedConfirmations(
                            confirmation)
                    .setMessageDetails(messageDetails)
                    .build();

            evidenceMessage.setMessageLaneId(businessMessage.getMessageLaneId());
            if (evidenceMessage.getMessageLaneId() == null) {
                evidenceMessage.setMessageLaneId(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
            }

            evidenceMessage.setConnectorMessageId(messageId);
            evidenceMessage.getMessageDetails().setCausedBy(businessMessage.getConnectorMessageId());
            LOGGER.info(
                    "Sending evidence as confirmation message with ID [{}]",
                    evidenceMessage.getConnectorMessageId()
            );

            return evidenceMessage;
        }
    }

    private void validateParameters(DomibusConnectorMessage businessMessage) {
        if (businessMessage == null) {
            throw new IllegalArgumentException("The businessMessage cannot be null here!");
        }
        if (businessMessage.getMessageDetails() == null) {
            throw new IllegalArgumentException("The messageDetails of the businessMessage cannot be null here!");
        }
        if (businessMessage.getMessageDetails().getDirection() == null) {
            throw new IllegalArgumentException("The direction is not allowed to be null here!");
        }
    }

    public boolean isSendCreatedTriggerEvidenceBack(DomibusConnectorBusinessDomain.BusinessDomainId messageDomain) {
        ConnectorMessageProcessingProperties processingProperties =
                configurationPropertyLoaderService.loadConfiguration(
                        messageDomain,
                        ConnectorMessageProcessingProperties.class
                );
        boolean result = processingProperties.isSendGeneratedEvidencesToBackend();
        LOGGER.debug("Evidence will be submitted back to Backend as EvidenceMessage: [{}]", result);
        return result;
    }
}
