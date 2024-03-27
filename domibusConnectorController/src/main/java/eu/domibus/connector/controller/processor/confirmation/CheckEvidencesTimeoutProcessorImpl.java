package eu.domibus.connector.controller.processor.confirmation;

import eu.domibus.connector.common.service.CurrentBusinessDomain;
import eu.domibus.connector.controller.exception.DomibusConnectorControllerException;
import eu.domibus.connector.controller.exception.DomibusConnectorMessageException;
import eu.domibus.connector.controller.spring.EvidencesTimeoutConfigurationProperties;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;


@Service
public class CheckEvidencesTimeoutProcessorImpl implements CheckEvidencesTimeoutProcessor {
    private static final Logger LOGGER = LogManager.getLogger(CheckEvidencesTimeoutProcessorImpl.class);
    private final EvidencesTimeoutConfigurationProperties evidencesTimeoutConfigurationProperties;
    private final DCMessagePersistenceService persistenceService;
    private final CreateEvidenceTimeoutConfirmationStep createEvidenceTimeoutConfirmationStep;

    public CheckEvidencesTimeoutProcessorImpl(
            EvidencesTimeoutConfigurationProperties evidencesTimeoutConfigurationProperties,
            DCMessagePersistenceService persistenceService,
            CreateEvidenceTimeoutConfirmationStep createEvidenceTimeoutConfirmationStep) {
        this.evidencesTimeoutConfigurationProperties = evidencesTimeoutConfigurationProperties;
        this.persistenceService = persistenceService;
        this.createEvidenceTimeoutConfirmationStep = createEvidenceTimeoutConfirmationStep;
    }

    @Override
    @Scheduled(fixedDelayString = "#{evidencesTimeoutConfigurationProperties.checkTimeout.milliseconds}")
    public void checkEvidencesTimeout() throws DomibusConnectorControllerException {
        LOGGER.info("Job for checking evidence timeouts triggered.");
        Date start = new Date();
        // only check for timeout of RELAY_REMMD_ACCEPTANCE/REJECTION evidences if the timeout is set in
        // the connector.properties
        if (evidencesTimeoutConfigurationProperties.getRelayREMMDTimeout().getMilliseconds() > 0 ||
                evidencesTimeoutConfigurationProperties.getRelayREMMDWarnTimeout().getMilliseconds() > 0) {
            checkNotRejectedNorConfirmedWithoutRelayREMMD();
        }

        // only check for timeout of DELIVERY/NON_DELIVERY evidences if the timeout is set in the connector.properties
        if (evidencesTimeoutConfigurationProperties.getDeliveryTimeout().getMilliseconds() > 0 ||
                evidencesTimeoutConfigurationProperties.getDeliveryWarnTimeout().getMilliseconds() > 0) {
            checkNotRejectedNorConfirmedAndWithoutDelivery();
        }

        LOGGER.debug(
                "Job for checking evidence timeouts finished in {} ms.",
                (System.currentTimeMillis() - start.getTime())
        );
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public void checkNotRejectedNorConfirmedWithoutRelayREMMD() throws DomibusConnectorControllerException {
        // Request database to get all messages not rejected and not confirmed yet and without
        // a RELAY_REMMD_ACCEPTANCE/REJECTION evidence
        List<DomibusConnectorMessage> messages =
                persistenceService.findOutgoingMessagesNotRejectedNorConfirmedAndWithoutRelayREMMD();
        LOGGER.trace("Checking [{}] messages for not rejected nor confirmed withoutRelayREMMD", messages.size());
        messages.forEach(this::checkNotRejectedNorConfirmedWithoutRelayREMMD);
    }

    void checkNotRejectedNorConfirmedWithoutRelayREMMD(DomibusConnectorMessage message) {
        String messageId = message.getConnectorMessageId().toString();
        try (
                org.slf4j.MDC.MDCCloseable mdcCloseable = org.slf4j.MDC.putCloseable(
                        LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME, messageId)
        ) {
            CurrentBusinessDomain.setCurrentBusinessDomain(message.getMessageLaneId());
            Duration relayREMMDTimeout = evidencesTimeoutConfigurationProperties.getRelayREMMDTimeout().getDuration();
            Duration relayREMMDWarnTimeout = evidencesTimeoutConfigurationProperties
                    .getRelayREMMDWarnTimeout()
                    .getDuration();

            // if it is later than the evaluated timeout given
            if (Duration.between(getDeliveryTime(message), Instant.now()).compareTo(relayREMMDTimeout) > 0) {
                try {
                    createEvidenceTimeoutConfirmationStep.createRelayRemmdFailureAndSendIt(message);
                    LOGGER.warn(
                            LoggingMarker.Log4jMarker.BUSINESS_LOG,
                            "Message [{}] reached relayREMMD timeout. A RelayREMMDFailure evidence " +
                                    "has been generated and sent.",
                            message.getConnectorMessageIdAsString()
                    );
                } catch (DomibusConnectorMessageException e) {
                    // throw new DomibusConnectorControllerException(e);
                    LOGGER.error("Exception occured while checking relayREMMDTimeout", e);
                }
                return;
            }
            if (Duration.between(getDeliveryTime(message), Instant.now()).compareTo(relayREMMDWarnTimeout) > 0) {
                LOGGER.warn(
                        LoggingMarker.Log4jMarker.BUSINESS_LOG,
                        "Message [{}] reached warning limit for relayREMMD confirmation timeout. " +
                                "No RelayREMMD evidence for this message has been received yet!",
                        message.getConnectorMessageId()
                );
            }
        } finally {
            CurrentBusinessDomain.setCurrentBusinessDomain(null);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public void checkNotRejectedNorConfirmedAndWithoutDelivery() throws DomibusConnectorControllerException {
        // Request database to get all messages not rejected yet and without a DELIVERY/NON_DELIVERY evidence
        List<DomibusConnectorMessage> messages = persistenceService
                .findOutgoingMessagesNotRejectedNorConfirmedAndWithoutDelivery();
        LOGGER.trace("Checking [{}] messages for confirmation timeout notRejectedWithoutDelivery", messages.size());
        messages.forEach(this::checkNotRejectedNorConfirmedAndWithoutDelivery);
    }

    void checkNotRejectedNorConfirmedAndWithoutDelivery(DomibusConnectorMessage message) {
        String messageId = message.getConnectorMessageId().toString();
        try (
                org.slf4j.MDC.MDCCloseable mdcCloseable = org.slf4j.MDC.putCloseable(
                        LoggingMDCPropertyNames
                                .MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME,
                        messageId
                )
        ) {
            CurrentBusinessDomain.setCurrentBusinessDomain(message.getMessageLaneId());
            LOGGER.trace("checkNotRejectedWithoutDelivery# checking message: [{}]");
            Duration deliveryTimeout = evidencesTimeoutConfigurationProperties.getDeliveryTimeout().getDuration();
            Duration deliveryWarnTimeout =
                    evidencesTimeoutConfigurationProperties.getDeliveryWarnTimeout().getDuration();

            // if it is later than the evaluated timeout given
            if (Duration.between(getDeliveryTime(message), Instant.now()).compareTo(deliveryTimeout) > 0) {
                try {
                    createEvidenceTimeoutConfirmationStep.createNonDeliveryAndSendIt(message);
                    LOGGER.warn(
                            LoggingMarker.Log4jMarker.BUSINESS_LOG,
                            "Message [{}] reached Delivery confirmation timeout. A NonDelivery evidence has " +
                                    "been generated and sent.",
                            message.getConnectorMessageIdAsString()
                    );
                } catch (DomibusConnectorMessageException e) {
                    throw new DomibusConnectorControllerException(e);
                }
                return;
            }
            if (Duration.between(getDeliveryTime(message), Instant.now()).compareTo(deliveryWarnTimeout) > 0) {
                LOGGER.warn(
                        LoggingMarker.Log4jMarker.BUSINESS_LOG,
                        "Message [{}] reached warning limit for delivery confirmation timeout. No Delivery " +
                                "evidence for this message has been received yet!",
                        message.getConnectorMessageId()
                );
            }
        } finally {
            CurrentBusinessDomain.setCurrentBusinessDomain(null);
        }
    }

    private Instant getDeliveryTime(DomibusConnectorMessage message) {
        DomibusConnectorMessageDetails details = message.getMessageDetails();
        if (details.getDirection() == null) {
            throw new IllegalArgumentException("Message direction is not set!");
        }
        Date deliveryDate;
        switch (details.getDirection()) {
            case GATEWAY_TO_BACKEND:
                deliveryDate = details.getDeliveredToBackend();
                break;
            case BACKEND_TO_GATEWAY:
                deliveryDate = details.getDeliveredToGateway();
                break;
            default:
                throw new IllegalStateException("Unknown message direction, cannot process any timeouts!");
        }
        return deliveryDate.toInstant();
    }
}
