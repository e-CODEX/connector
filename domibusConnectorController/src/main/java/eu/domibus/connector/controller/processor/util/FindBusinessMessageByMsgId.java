/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.processor.util;

import eu.domibus.connector.controller.exception.DomibusConnectorMessageException;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for finding a business message by its message ID and direction.
 */
@Component
public class FindBusinessMessageByMsgId {
    private static final Logger LOGGER = LogManager.getLogger(FindBusinessMessageByMsgId.class);
    private final DCMessagePersistenceService msgPersistenceService;

    public FindBusinessMessageByMsgId(DCMessagePersistenceService msgPersistenceService) {
        this.msgPersistenceService = msgPersistenceService;
    }

    /**
     * Finds a business message by its message ID and direction.
     *
     * @param refMessage  The reference message used to find the business message
     * @param direction   The direction of the business message (INBOUND or OUTBOUND)
     * @return The found business message
     * @throws DomibusConnectorMessageException if the related message cannot be found
     */
    public DomibusConnectorMessage findBusinessMessageByIdAndDirection(
        DomibusConnectorMessage refMessage, DomibusConnectorMessageDirection direction) {

        DomibusConnectorMessageDetails messageDetails = refMessage.getMessageDetails();
        String refToEbmsId = messageDetails.getRefToMessageId();

        Optional<DomibusConnectorMessage> messageByEbmsIdOrBackendIdAndDirection =
            msgPersistenceService.findMessageByEbmsIdOrBackendIdAndDirection(
                refToEbmsId,
                direction
            );
        if (messageByEbmsIdOrBackendIdAndDirection.isPresent()) {
            LOGGER.debug("Successfully used refToMessageId [{}] to find business msg", refToEbmsId);
            return messageByEbmsIdOrBackendIdAndDirection.get();
        }

        String refToBackendId = messageDetails.getRefToBackendMessageId();
        messageByEbmsIdOrBackendIdAndDirection =
            msgPersistenceService.findMessageByEbmsIdOrBackendIdAndDirection(
                refToBackendId,
                direction
            );
        if (messageByEbmsIdOrBackendIdAndDirection.isPresent()) {
            LOGGER.debug(
                "Successfully used refToBackendMessageId [{}] to find business msg",
                refToBackendId
            );
            return messageByEbmsIdOrBackendIdAndDirection.get();
        }

        var error = String.format(
            "Was not able to find related message for refToEbmsId [%s] or refToBackendId [%s] "
                + "and direction [%s]!",
            refToEbmsId, refToBackendId, direction
        );
        throw new DomibusConnectorMessageException(
            refMessage, FindBusinessMessageByMsgId.class, error);
    }
}
