/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.processor.content;

import eu.ecodex.connector.controller.spring.ContentDeletionConfigurationProperties;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;
import eu.ecodex.connector.domain.model.LargeFileReference;
import eu.ecodex.connector.lib.logging.MDC;
import eu.ecodex.connector.persistence.service.DCMessagePersistenceService;
import eu.ecodex.connector.persistence.service.LargeFilePersistenceService;
import eu.ecodex.connector.persistence.service.exceptions.LargeFileDeletionException;
import eu.ecodex.connector.tools.LoggingMDCPropertyNames;
import eu.ecodex.connector.tools.logging.LoggingMarker;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * This service is triggered by a timer job
 * and is responsible for deleting.
 * large file storage references if the reference is
 * older than a day and has no associated business message
 * within the database or the associated business message
 * has already been confirmed or rejected
 */
@Service
@ConditionalOnProperty(
    prefix = ContentDeletionConfigurationProperties.PREFIX,
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class CheckContentDeletedProcessorImpl {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(CheckContentDeletedProcessorImpl.class);
    private final LargeFilePersistenceService largeFilePersistenceService;
    private final DCMessagePersistenceService messagePersistenceService;

    public CheckContentDeletedProcessorImpl(LargeFilePersistenceService largeFilePersistenceService,
                                            DCMessagePersistenceService messagePersistenceService) {
        this.largeFilePersistenceService = largeFilePersistenceService;
        this.messagePersistenceService = messagePersistenceService;
    }

    /**
     * This method is scheduled to run at a fixed delay specified by
     * the 'checkTimeout.milliseconds' property.
     * It checks all available references for deletion by calling
     * the 'getAllAvailableReferences' method of the 'largeFilePersistenceService' object.
     * It then logs the references being checked and iterates through each reference to perform
     * the 'checkDelete' operation.
     */
    @Scheduled(
        fixedDelayString = "#{" + ContentDeletionConfigurationProperties.BEAN_NAME
            + ".checkTimeout.milliseconds}"
    )
    @MDC(
        name = LoggingMDCPropertyNames.MDC_DC_MESSAGE_PROCESSOR_PROPERTY_NAME,
        value = "checkContentDeletedProcessor"
    )
    public void checkContentDeletedProcessor() {
        Map<DomibusConnectorMessageId, List<LargeFileReference>> allAvailableReferences =
            largeFilePersistenceService.getAllAvailableReferences();
        LOGGER.debug("Checking the following references [{}] for deletion", allAvailableReferences);
        allAvailableReferences.forEach(this::checkDelete);
    }

    private void checkDelete(DomibusConnectorMessageId id, List<LargeFileReference> references) {
        DomibusConnectorMessage msg =
            messagePersistenceService.findMessageByConnectorMessageId(id.getConnectorMessageId());
        String messageIdString =
            msg == null ? "" : msg.getConnectorMessageId().getConnectorMessageId();
        try (var mdcCloseable = org.slf4j.MDC.putCloseable(
            LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME,
            messageIdString
        )) {
            if (msg == null) {
                LOGGER.debug(
                    "No message with connector message id [{}] found in database deleting "
                        + "references only when older as 1 day",
                    id
                );
                ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
                // delete only refs which are older than one day
                List<LargeFileReference> collect = references.stream()
                    .filter(
                        r -> r.getCreationDate() != null && r.getCreationDate().isBefore(yesterday))
                    .toList();
                LOGGER.debug(
                    "Deleting references [{}] with no associated business message", collect);
                collect.forEach(this::deleteReference);
            } else if (msg.getMessageDetails().getConfirmed() != null
                || msg.getMessageDetails().getRejected() != null) {
                // delete refs when business msg already rejected or confirmed
                LOGGER.debug(
                    "Message with id [{}] already confirmed/rejected - deleting content [{}]", id,
                    references
                );
                references.forEach(this::deleteReference);
            } else {
                LOGGER.debug(
                    "Message with connector message id [{}] is older then 7 days. "
                        + "Deleting reference anyway.",
                    id
                );
                ZonedDateTime oneWeekAgo = ZonedDateTime.now().minusDays(7);
                // delete only refs which are older than 7 days
                List<LargeFileReference> collect = references.stream()
                    .filter(r -> r.getCreationDate() != null
                        && r.getCreationDate().isBefore(oneWeekAgo)).toList();
                LOGGER.debug(
                    "Deleting references [{}] where associated business message is "
                        + "older then 7 days",
                    collect
                );
                collect.forEach(this::deleteReference);
            }
        }
    }

    private void deleteReference(LargeFileReference ref) {
        LOGGER.debug("Deleting reference with id [{}]", ref.getStorageIdReference());
        try {
            largeFilePersistenceService.deleteDomibusConnectorBigDataReference(ref);
        } catch (LargeFileDeletionException delException) {
            LOGGER.error(
                LoggingMarker.BUSINESS_CONTENT_LOG,
                "Was unable to delete the reference [{}] in the timer job. The data must "
                    + "be manually deleted by the administrator!",
                ref
            );
            LOGGER.error("Was unable to delete due exception: ", delException);
        }
    }
}
