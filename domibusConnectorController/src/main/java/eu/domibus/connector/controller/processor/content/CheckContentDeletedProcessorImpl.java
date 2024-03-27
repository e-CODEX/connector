package eu.domibus.connector.controller.processor.content;

import eu.domibus.connector.controller.spring.ContentDeletionConfigurationProperties;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.lib.logging.MDC;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import eu.domibus.connector.persistence.service.LargeFilePersistenceService;
import eu.domibus.connector.persistence.service.exceptions.LargeFileDeletionException;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * This service is triggered by an timer job
 * and is responsible for deleting
 * large file storage references if the reference is
 * older than a day and has no associated business message
 * within the database or the associated business message
 * has already been confirmed or rejected
 */
@Service
@ConditionalOnProperty(
        prefix = ContentDeletionConfigurationProperties.PREFIX, name = "enabled",
        havingValue = "true", matchIfMissing = true
)
public class CheckContentDeletedProcessorImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckContentDeletedProcessorImpl.class);
    private final LargeFilePersistenceService largeFilePersistenceService;
    private final DCMessagePersistenceService messagePersistenceService;

    public CheckContentDeletedProcessorImpl(
            LargeFilePersistenceService largeFilePersistenceService,
            DCMessagePersistenceService messagePersistenceService) {
        this.largeFilePersistenceService = largeFilePersistenceService;
        this.messagePersistenceService = messagePersistenceService;
    }

    @Scheduled(
            fixedDelayString = "#{" + ContentDeletionConfigurationProperties.BEAN_NAME + ".checkTimeout.milliseconds}"
    )
    @MDC(name = LoggingMDCPropertyNames.MDC_DC_MESSAGE_PROCESSOR_PROPERTY_NAME, value = "checkContentDeletedProcessor")
    public void checkContentDeletedProcessor() {
        Map<DomibusConnectorMessageId, List<LargeFileReference>> allAvailableReferences =
                largeFilePersistenceService.getAllAvailableReferences();
        LOGGER.debug("Checking the following references [{}] for deletion", allAvailableReferences);
        allAvailableReferences.forEach(this::checkDelete);
    }

    private void checkDelete(DomibusConnectorMessageId id, List<LargeFileReference> references) {
        DomibusConnectorMessage msg = messagePersistenceService
                .findMessageByConnectorMessageId(id.getConnectorMessageId());
        String messageIdString = msg == null ? "" : msg.getConnectorMessageId().getConnectorMessageId();
        try (
                org.slf4j.MDC.MDCCloseable mdcCloseable = org.slf4j.MDC.putCloseable(
                        LoggingMDCPropertyNames.MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME, messageIdString)
        ) {
            if (msg == null) {
                LOGGER.debug("No message with connector message id [{}] found in database deleting references only " +
                                     "when older as 1 day", id
                );
                ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
                // delete only refs which are older than one day
                List<LargeFileReference> collect = references.stream()
                                                             .filter(r -> r.getCreationDate() != null && r
                                                                     .getCreationDate().isBefore(yesterday))
                                                             .collect(Collectors.toList());
                LOGGER.debug("Deleting references [{}] with no associated business message", collect);
                collect.forEach(this::deleteReference);
            } else if (msg.getMessageDetails().getConfirmed() != null || msg.getMessageDetails()
                                                                            .getRejected() != null) {
                // delete refs when business msg already rejected or confirmed
                LOGGER.debug("Message with id [{}] already confirmed/rejected - deleting content [{}]", id, references);
                references.stream()
                          .forEach(this::deleteReference);
            } else {
                LOGGER.debug(
                        "Message with connector message id [{}] is older then 7 days. Deleting reference anyway.", id
                );
                ZonedDateTime oneWeekAgo = ZonedDateTime.now().minusDays(7);
                // delete only refs which are older than 7 days
                List<LargeFileReference> collect = references.stream()
                                                             .filter(r -> r.getCreationDate() != null && r
                                                                     .getCreationDate().isBefore(oneWeekAgo))
                                                             .collect(Collectors.toList());
                LOGGER.debug(
                        "Deleting references [{}] where associated business message is older then 7 days", collect
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
                    LoggingMarker.BUSINESS_CONTENT_LOG, "Was unable to delete the reference [{}] in the timer job. " +
                            "The data must be manually deleted by the administrator!",
                    ref
            );
            LOGGER.error("Was unable to delete due exception: ", delException);
        }
    }
}
