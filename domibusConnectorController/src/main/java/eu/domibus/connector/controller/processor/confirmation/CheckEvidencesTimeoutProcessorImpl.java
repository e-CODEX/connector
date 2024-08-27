/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

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
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The CheckEvidencesTimeoutProcessorImpl class is an implementation of
 * the CheckEvidencesTimeoutProcessor interface.
 * It provides a method to check the timeout for evidences.
 */
@Service
public class CheckEvidencesTimeoutProcessorImpl implements CheckEvidencesTimeoutProcessor {
    private static final Logger LOGGER = LogManager.getLogger(
        CheckEvidencesTimeoutProcessorImpl.class
    );
    private final EvidencesTimeoutConfigurationProperties evidencesTimeoutConfigurationProperties;
    private final DCMessagePersistenceService persistenceService;
    private final CreateEvidenceTimeoutConfirmationStep createEvidenceTimeoutConfirmationStep;

    /**
     * CheckEvidencesTimeoutProcessorImpl is a class that handles the checking of message timeouts
     * and initiates further processing based on the configuration properties.
     * This class requires an instance of EvidencesTimeoutConfigurationProperties,
     * DCMessagePersistenceService, and CreateEvidenceTimeoutConfirmationStep to be initialized.
     */
    public CheckEvidencesTimeoutProcessorImpl(
        EvidencesTimeoutConfigurationProperties evidencesTimeoutConfigurationProperties,
        DCMessagePersistenceService persistenceService,
        CreateEvidenceTimeoutConfirmationStep createEvidenceTimeoutConfirmationStep) {
        this.evidencesTimeoutConfigurationProperties = evidencesTimeoutConfigurationProperties;
        this.persistenceService = persistenceService;
        this.createEvidenceTimeoutConfirmationStep = createEvidenceTimeoutConfirmationStep;
    }

    @Override
    @Scheduled(
        fixedDelayString = "#{evidencesTimeoutConfigurationProperties.checkTimeout.milliseconds}"
    )
    public void checkEvidencesTimeout() throws DomibusConnectorControllerException {
        LOGGER.info("Job for checking evidence timeouts triggered.");
        var start = new Date();

        // only check for timeout of RELAY_REMMD_ACCEPTANCE/REJECTION evidences if the timeout
        // is set in the connector.properties
        if (evidencesTimeoutConfigurationProperties.getRelayREMMDTimeout().getMilliseconds() > 0
            || evidencesTimeoutConfigurationProperties.getRelayREMMDWarnTimeout()
            .getMilliseconds() > 0) {
            checkNotRejectedNorConfirmedWithoutRelayREMMD();
        }

        // only check for timeout of DELIVERY/NON_DELIVERY evidences if the timeout is set
        // in the connector.properties
        if (evidencesTimeoutConfigurationProperties.getDeliveryTimeout().getMilliseconds() > 0
            || evidencesTimeoutConfigurationProperties.getDeliveryWarnTimeout()
            .getMilliseconds() > 0) {
            checkNotRejectedNorConfirmedAndWithoutDelivery();
        }

        LOGGER.debug(
            "Job for checking evidence timeouts finished in {} ms.",
            (System.currentTimeMillis() - start.getTime())
        );
    }

    /**
     * Retrieves all messages that are not rejected, not confirmed yet, and without
     * a RELAY_REMMD_ACCEPTANCE/REJECTION evidence from the database.
     * It then iterates through each message and calls
     * the {@link #checkNotRejectedNorConfirmedWithoutRelayREMMD(DomibusConnectorMessage)} method.
     *
     * @throws DomibusConnectorControllerException if an error occurs during the process
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public void checkNotRejectedNorConfirmedWithoutRelayREMMD()
        throws DomibusConnectorControllerException {
        // Request database to get all messages not rejected and not confirmed yet and without a
        // RELAY_REMMD_ACCEPTANCE/REJECTION evidence
        List<DomibusConnectorMessage> messages =
            persistenceService.findOutgoingMessagesNotRejectedNorConfirmedAndWithoutRelayREMMD();
        LOGGER.trace(
            "Checking [{}] messages for not rejected nor confirmed withoutRelayREMMD",
            messages.size()
        );
        messages.forEach(this::checkNotRejectedNorConfirmedWithoutRelayREMMD);
    }

    void checkNotRejectedNorConfirmedWithoutRelayREMMD(DomibusConnectorMessage message) {
        var messageId = message.getConnectorMessageId().toString();
        try (var mdcCloseable = org.slf4j.MDC.putCloseable(
            LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME, messageId)) {
            CurrentBusinessDomain.setCurrentBusinessDomain(message.getMessageLaneId());
            var relayREMMDTimeout =
                evidencesTimeoutConfigurationProperties.getRelayREMMDTimeout().getDuration();
            var relayREMMDWarnTimeout =
                evidencesTimeoutConfigurationProperties.getRelayREMMDWarnTimeout().getDuration();

            // if it is later than the evaluated timeout given
            if (Duration.between(getDeliveryTime(message), Instant.now())
                .compareTo(relayREMMDTimeout) > 0) {
                try {
                    createEvidenceTimeoutConfirmationStep.createRelayRemmdFailureAndSendIt(message);
                    String warning = "Message [{}] reached relayREMMD timeout. A RelayREMMDFailure "
                        + "evidence has been generated and sent.";
                    LOGGER.warn(
                        LoggingMarker.Log4jMarker.BUSINESS_LOG,
                        warning,
                        message.getConnectorMessageIdAsString()
                    );
                } catch (DomibusConnectorMessageException e) {
                    // throw new DomibusConnectorControllerException(e);
                    LOGGER.error("Exception occurred while checking relayREMMDTimeout", e);
                }
                return;
            }
            if (Duration.between(getDeliveryTime(message), Instant.now())
                .compareTo(relayREMMDWarnTimeout) > 0) {
                String warning = "Message [{}] reached warning limit for relayREMMD confirmation "
                    + "timeout. No RelayREMMD evidence for this message has been received yet!";
                LOGGER.warn(
                    LoggingMarker.Log4jMarker.BUSINESS_LOG,
                    warning,
                    message.getConnectorMessageId()
                );
            }
        } finally {
            CurrentBusinessDomain.setCurrentBusinessDomain(null);
        }
    }

    /**
     * Retrieves all messages that are not rejected, not confirmed yet, and without
     * a DELIVERY/NON_DELIVERY evidence from the database.
     * It then iterates through each message and calls the
     * {@link #checkNotRejectedNorConfirmedAndWithoutDelivery(DomibusConnectorMessage)}
     * method.
     *
     * @throws DomibusConnectorControllerException if an error occurs during the process
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public void checkNotRejectedNorConfirmedAndWithoutDelivery()
        throws DomibusConnectorControllerException {
        // Request database to get all messages not rejected yet and without a DELIVERY/NON_DELIVERY
        // evidence
        List<DomibusConnectorMessage> messages =
            persistenceService.findOutgoingMessagesNotRejectedNorConfirmedAndWithoutDelivery();
        LOGGER.trace(
            "Checking [{}] messages for confirmation timeout notRejectedWithoutDelivery",
            messages.size()
        );
        messages.forEach(this::checkNotRejectedNorConfirmedAndWithoutDelivery);
    }

    void checkNotRejectedNorConfirmedAndWithoutDelivery(DomibusConnectorMessage message) {
        var messageId = message.getConnectorMessageId().toString();
        try (var mdcCloseable = org.slf4j.MDC.putCloseable(
            LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME, messageId)) {
            CurrentBusinessDomain.setCurrentBusinessDomain(message.getMessageLaneId());
            LOGGER.trace("checkNotRejectedWithoutDelivery# checking message: [{}]");
            var deliveryTimeout =
                evidencesTimeoutConfigurationProperties.getDeliveryTimeout().getDuration();
            var deliveryWarnTimeout =
                evidencesTimeoutConfigurationProperties.getDeliveryWarnTimeout().getDuration();

            // if it is later than the evaluated timeout given
            if (Duration.between(getDeliveryTime(message), Instant.now())
                .compareTo(deliveryTimeout) > 0) {
                try {
                    createEvidenceTimeoutConfirmationStep.createNonDeliveryAndSendIt(message);
                    LOGGER.warn(
                        LoggingMarker.Log4jMarker.BUSINESS_LOG,
                        "Message [{}] reached Delivery confirmation timeout."
                            + "A NonDelivery evidence has been generated and sent.",
                        message.getConnectorMessageIdAsString()
                    );
                } catch (DomibusConnectorMessageException e) {
                    throw new DomibusConnectorControllerException(e);
                }
                return;
            }
            if (Duration.between(getDeliveryTime(message), Instant.now())
                .compareTo(deliveryWarnTimeout) > 0) {
                LOGGER.warn(
                    LoggingMarker.Log4jMarker.BUSINESS_LOG,
                    "Message [{}] reached warning limit for delivery confirmation "
                        + "timeout. No Delivery evidence for this message has been received yet!",
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
                throw new IllegalStateException(
                    "Unknown message direction, cannot process any timeouts!");
        }
        return deliveryDate.toInstant();
    }
}
