package eu.domibus.connector.persistence.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.domibus.connector.common.annotations.DomainModelJsonObjectMapper;
import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.DomibusConnectorTransportStep;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.domibus.connector.persistence.dao.CommonPersistenceTest;
import eu.domibus.connector.persistence.model.PDomibusConnectorTransportStep;
import eu.domibus.connector.persistence.service.TransportStepPersistenceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@CommonPersistenceTest
class TransportStepPersistenceServiceImplITCase {
    @Autowired
    DataSource ds;
    @Autowired
    TransportStepPersistenceService transportStepPersistenceService;
    @DomainModelJsonObjectMapper
    ObjectMapper objectMapper;

    @Test
    void testMessageProcessingWhenTransportedMsgIsNull() {
        final PDomibusConnectorTransportStep domibusConnectorTransportStep = new PDomibusConnectorTransportStep();
        domibusConnectorTransportStep.setTransportId(new TransportStateService.TransportId("id"));
        domibusConnectorTransportStep.setConnectorMessageId("bar");
        domibusConnectorTransportStep.setTransportedMessage(null);
        Assertions.assertDoesNotThrow(() -> ((TransportStepPersistenceServiceImpl) transportStepPersistenceService).mapTransportStepToDomain(
                domibusConnectorTransportStep));
    }

    @Test
    void testMessageProcessingWhenTransportedMsgIsEmptyJson() {
        final PDomibusConnectorTransportStep domibusConnectorTransportStep = new PDomibusConnectorTransportStep();
        domibusConnectorTransportStep.setTransportId(new TransportStateService.TransportId("id"));
        domibusConnectorTransportStep.setConnectorMessageId("bar");
        domibusConnectorTransportStep.setTransportedMessage("{}");
        Assertions.assertDoesNotThrow(() -> ((TransportStepPersistenceServiceImpl) transportStepPersistenceService).mapTransportStepToDomain(
                domibusConnectorTransportStep));
    }

    @Test
    void testMessageProcessingWhenTransportedMsgIsEmptyJsonArray() {
        final PDomibusConnectorTransportStep domibusConnectorTransportStep = new PDomibusConnectorTransportStep();
        domibusConnectorTransportStep.setTransportId(new TransportStateService.TransportId("id"));
        domibusConnectorTransportStep.setConnectorMessageId("bar");
        domibusConnectorTransportStep.setTransportedMessage("[]");
        Assertions.assertDoesNotThrow(() -> ((TransportStepPersistenceServiceImpl) transportStepPersistenceService).mapTransportStepToDomain(
                domibusConnectorTransportStep));
    }

    @Test
    void createNewTransportStep() {
        DomibusConnectorMessage m = DomainEntityCreator.createMessage();
        m.setConnectorMessageId(new DomibusConnectorMessageId("id002"));

        DomibusConnectorTransportStep step = new DomibusConnectorTransportStep();
        DomibusConnectorLinkPartner.LinkPartnerName lp = new DomibusConnectorLinkPartner.LinkPartnerName("link2");

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
        DomibusConnectorLinkPartner.LinkPartnerName lp = new DomibusConnectorLinkPartner.LinkPartnerName("link4");

        DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate statusUpdate =
                new DomibusConnectorTransportStep.DomibusConnectorTransportStepStatusUpdate();
        statusUpdate.setTransportState(TransportState.PENDING);
        statusUpdate.setCreated(LocalDateTime.now());

        step.setTransportedMessage(m);
        step.setTransportId(new TransportStateService.TransportId("msg3_link2_1"));
        step.setAttempt(1);
        step.setLinkPartnerName(lp);
        step.addStatusUpdate(statusUpdate);

        transportStepPersistenceService.createNewTransportStep(step);

        List<DomibusConnectorTransportStep> pendingStepBy = transportStepPersistenceService.findPendingStepBy(lp);
        assertThat(pendingStepBy).hasSize(1);
    }
}
