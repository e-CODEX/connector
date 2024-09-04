/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.helper;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageDetails;
import eu.ecodex.connector.domain.model.DomibusConnectorParty;
import eu.ecodex.connector.domain.testutil.DomainEntityCreator;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class DomainModelHelperTest {
    @Test
    void testIsEvidenceMessage() {
        DomibusConnectorMessage createSimpleTestMessage =
            DomainEntityCreator.createSimpleTestMessage();
        boolean isEvidenceMessage = DomainModelHelper.isEvidenceMessage(createSimpleTestMessage);
        assertThat(isEvidenceMessage).isFalse();
    }

    @Test
    void testIsEvidenceTriggerMessage_shouldBeFalse() {
        DomibusConnectorMessage createSimpleTestMessage =
            DomainEntityCreator.createEvidenceNonDeliveryMessage();
        createSimpleTestMessage.getTransportedMessageConfirmations()
                               .getFirst().setEvidence("evidence".getBytes(StandardCharsets.UTF_8));
        boolean isEvidenceMessage =
            DomainModelHelper.isEvidenceTriggerMessage(createSimpleTestMessage);
        assertThat(isEvidenceMessage).isFalse();
    }

    @Test
    void testIsEvidenceTriggerMessage_shouldBeTrue() {
        DomibusConnectorMessage createSimpleTestMessage =
            DomainEntityCreator.createEvidenceNonDeliveryMessage();
        createSimpleTestMessage.getTransportedMessageConfirmations()
                               .getFirst().setEvidence(null);
        boolean isEvidenceMessage =
            DomainModelHelper.isEvidenceTriggerMessage(createSimpleTestMessage);
        assertThat(isEvidenceMessage).isTrue();
    }

    @Test
    void testIsEvidenceTriggerMessageEmptyByteArray_shouldBeTrue() {
        DomibusConnectorMessage createSimpleTestMessage =
            DomainEntityCreator.createEvidenceNonDeliveryMessage();
        createSimpleTestMessage.getTransportedMessageConfirmations()
                               .getFirst().setEvidence(new byte[0]);
        boolean isEvidenceMessage =
            DomainModelHelper.isEvidenceTriggerMessage(createSimpleTestMessage);
        assertThat(isEvidenceMessage).isTrue();
    }

    @Test
    void testIsEvidenceTriggerMessage_businessMsg_shouldBeFalse() {
        DomibusConnectorMessage createSimpleTestMessage =
            DomainEntityCreator.createSimpleTestMessage();
        boolean isEvidenceMessage =
            DomainModelHelper.isEvidenceTriggerMessage(createSimpleTestMessage);
        assertThat(isEvidenceMessage).isFalse();
    }

    @Test
    void switchMessageDirection() {
        DomibusConnectorMessage origMsg = DomainEntityCreator.createEpoMessage();
        origMsg.getMessageDetails()
               .setDirection(DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);
        DomibusConnectorMessageDetails swMsgDetails =
            DomainModelHelper.switchMessageDirection(origMsg.getMessageDetails());

        swMsgDetails.getFromParty().setRole("fromRole");
        swMsgDetails.getToParty().setRole("toRole");

        DomibusConnectorMessageDetails origMsgDetails = origMsg.getMessageDetails();

        // assert direction is switched
        assertThat(swMsgDetails.getDirection()).isEqualTo(
            DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);
        // assert that party is switched
        assertThat(swMsgDetails.getFromParty()).isNotNull();
        assertThat(swMsgDetails.getFromParty()).isEqualToComparingOnlyGivenFields(
            origMsgDetails.getToParty(), "partyId", "partyIdType");
        assertThat(swMsgDetails.getFromParty().getRoleType())
            .as("party role type of from party is always initiator")
            .isEqualTo(DomibusConnectorParty.PartyRoleType.INITIATOR);
        assertThat(swMsgDetails.getFromParty().getRole())
            .as("party role of from party is always fromRole")
            .isEqualTo("fromRole");
        assertThat(swMsgDetails.getToParty()).isNotNull();
        assertThat(swMsgDetails.getToParty())
            .isEqualToComparingOnlyGivenFields(
                origMsgDetails.getFromParty(), "partyId", "partyIdType");
        assertThat(swMsgDetails.getToParty().getRoleType())
            .as("party role type of to party is always responder")
            .isEqualTo(DomibusConnectorParty.PartyRoleType.RESPONDER);
        assertThat(swMsgDetails.getToParty().getRole())
            .as("party role of to party is always toRole")
            .isEqualTo("toRole");

        // assertThat final recipient/original sender is switched
        assertThat(swMsgDetails.getFinalRecipient()).isEqualTo(origMsgDetails.getOriginalSender());
        assertThat(swMsgDetails.getOriginalSender()).isEqualTo(origMsgDetails.getFinalRecipient());
    }
}
