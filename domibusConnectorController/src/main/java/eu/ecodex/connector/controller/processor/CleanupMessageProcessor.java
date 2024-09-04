/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.processor;

import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.persistence.service.DCMessageContentManager;
import org.springframework.stereotype.Service;

/**
 * This processor is called after a business message
 * has been rejected or confirmed.
 *
 * <p>delegates deletion of message content
 */
@Service
public class CleanupMessageProcessor implements DomibusConnectorMessageProcessor {
    private final DCMessageContentManager dcMessageContentManager;

    public CleanupMessageProcessor(DCMessageContentManager dcMessageContentManager) {
        this.dcMessageContentManager = dcMessageContentManager;
    }

    @Override
    public void processMessage(DomibusConnectorMessage message) {
        dcMessageContentManager.cleanForMessage(message);
    }
}
