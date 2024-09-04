/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.processor.util;

import eu.ecodex.connector.controller.exception.DomibusConnectorMessageException;
import eu.ecodex.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageDetails;
import eu.ecodex.connector.persistence.service.DCMessagePersistenceService;
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
