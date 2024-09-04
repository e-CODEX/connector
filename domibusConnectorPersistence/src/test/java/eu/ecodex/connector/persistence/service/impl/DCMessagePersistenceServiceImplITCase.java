/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;
import eu.ecodex.connector.domain.testutil.DomainEntityCreator;
import eu.ecodex.connector.persistence.dao.CommonPersistenceTest;
import eu.ecodex.connector.persistence.service.DCMessagePersistenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

@CommonPersistenceTest
class DCMessagePersistenceServiceImplITCase {
    @Autowired
    DCMessagePersistenceService persistenceService;
    @Autowired
    TransactionTemplate txTemplate;

    @Test
    void testPersistLoadBusinessMessage() {
        DomibusConnectorMessage epoMessage = DomainEntityCreator.createEpoMessage();
        epoMessage.setConnectorMessageId(new DomibusConnectorMessageId("id1"));
        epoMessage.getMessageDetails()
                  .setDirection(DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);

        txTemplate.executeWithoutResult(
            t -> persistenceService.persistBusinessMessageIntoDatabase(epoMessage));

        DomibusConnectorMessage loadedBusinessMsg =
            persistenceService.findMessageByConnectorMessageId("id1");
        assertThat(loadedBusinessMsg).isNotNull();
    }

    @Test
    void testPersistEvidenceMessage_shouldThrow() {
        DomibusConnectorMessage evidenceMsg =
            DomainEntityCreator.createEvidenceNonDeliveryMessage();
        evidenceMsg.getMessageDetails().setCausedBy(new DomibusConnectorMessageId("id1"));

        evidenceMsg.setConnectorMessageId(new DomibusConnectorMessageId("ev1"));
        evidenceMsg.getMessageDetails()
                   .setDirection(DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);

        txTemplate.executeWithoutResult(
            t -> persistenceService.persistBusinessMessageIntoDatabase(evidenceMsg));
    }
}
