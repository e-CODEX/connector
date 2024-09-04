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

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.ecodex.connector.common.annotations.DomainModelJsonObjectMapper;
import eu.ecodex.connector.controller.service.TransportStateService;
import eu.ecodex.connector.domain.enums.TransportState;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;
import eu.ecodex.connector.domain.model.DomibusConnectorTransportStep;
import eu.ecodex.connector.domain.testutil.DomainEntityCreator;
import eu.ecodex.connector.persistence.dao.CommonPersistenceTest;
import eu.ecodex.connector.persistence.model.PDomibusConnectorTransportStep;
import eu.ecodex.connector.persistence.service.TransportStepPersistenceService;
import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@CommonPersistenceTest
class TransportStepPersistenceServiceImplITCase {
    @Autowired
    DataSource dataSource;
    @Autowired
    TransportStepPersistenceService transportStepPersistenceService;
    @DomainModelJsonObjectMapper
    ObjectMapper objectMapper;

    @Test
    void testMessageProcessingWhenTransportedMsgIsNull() {
        final PDomibusConnectorTransportStep domibusConnectorTransportStep =
            new PDomibusConnectorTransportStep();
        domibusConnectorTransportStep.setTransportId(new TransportStateService.TransportId("id"));
        domibusConnectorTransportStep.setConnectorMessageId("bar");
        domibusConnectorTransportStep.setTransportedMessage(null);
        Assertions.assertDoesNotThrow(
            () -> ((TransportStepPersistenceServiceImpl) transportStepPersistenceService)
                .mapTransportStepToDomain(domibusConnectorTransportStep));
    }

    @Test
    void testMessageProcessingWhenTransportedMsgIsEmptyJson() {
        final PDomibusConnectorTransportStep domibusConnectorTransportStep =
            new PDomibusConnectorTransportStep();
        domibusConnectorTransportStep.setTransportId(new TransportStateService.TransportId("id"));
        domibusConnectorTransportStep.setConnectorMessageId("bar");
        domibusConnectorTransportStep.setTransportedMessage("{}");
        Assertions.assertDoesNotThrow(
            () -> ((TransportStepPersistenceServiceImpl) transportStepPersistenceService)
                .mapTransportStepToDomain(domibusConnectorTransportStep));
    }

    @Test
    void testMessageProcessingWhenTransportedMsgIsEmptyJsonArray() {
        final PDomibusConnectorTransportStep domibusConnectorTransportStep =
            new PDomibusConnectorTransportStep();
        domibusConnectorTransportStep.setTransportId(new TransportStateService.TransportId("id"));
        domibusConnectorTransportStep.setConnectorMessageId("bar");
        domibusConnectorTransportStep.setTransportedMessage("[]");
        Assertions.assertDoesNotThrow(
            () -> ((TransportStepPersistenceServiceImpl) transportStepPersistenceService)
                .mapTransportStepToDomain(domibusConnectorTransportStep));
    }

    @Test
    void createNewTransportStep() {
        DomibusConnectorMessage m = DomainEntityCreator.createMessage();
        m.setConnectorMessageId(new DomibusConnectorMessageId("id002"));

        DomibusConnectorTransportStep step = new DomibusConnectorTransportStep();
        DomibusConnectorLinkPartner.LinkPartnerName lp =
            new DomibusConnectorLinkPartner.LinkPartnerName("link2");

        step.setConnectorMessageId(new DomibusConnectorMessageId("id002"));
        step.setTransportedMessage(m);
        step.setTransportId(new TransportStateService.TransportId("msg2_link2_1"));
        step.setAttempt(1);
        step.setLinkPartnerName(lp);

        transportStepPersistenceService.createNewTransportStep(step);
    }

    @Test
    void createNewTransportStepSetPending() {
        DomibusConnectorMessage m = DomainEntityCreator.createMessage();
        m.setConnectorMessageId(new DomibusConnectorMessageId("id002"));

        DomibusConnectorTransportStep step = new DomibusConnectorTransportStep();

        DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate statusUpdate =
            new DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate();
        statusUpdate.setTransportState(TransportState.PENDING);
        statusUpdate.setCreated(LocalDateTime.now());

        var lp = new DomibusConnectorLinkPartner.LinkPartnerName("link4");

        step.setTransportedMessage(m);
        step.setTransportId(new TransportStateService.TransportId("msg3_link2_1"));
        step.setAttempt(1);
        step.setLinkPartnerName(lp);
        step.addStatusUpdate(statusUpdate);

        transportStepPersistenceService.createNewTransportStep(step);

        List<DomibusConnectorTransportStep> pendingStepBy =
            transportStepPersistenceService.findPendingStepBy(lp);
        assertThat(pendingStepBy).hasSize(1);
    }
}
