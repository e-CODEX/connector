package eu.domibus.connector.domain.model.helper;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;


/**
 *
 */
class DomainModelHelperTest {
    public DomainModelHelperTest() {
    }

    @Test
    void testIsEvidenceMessage() {
        DomibusConnectorMessage createSimpleTestMessage = DomainEntityCreator.createSimpleTestMessage();
        boolean isEvidenceMessage = DomainModelHelper.isEvidenceMessage(createSimpleTestMessage);
        assertThat(isEvidenceMessage).isFalse();
    }

    @Test
    void testIsEvidenceTriggerMessage_shouldBeFalse() {
        DomibusConnectorMessage createSimpleTestMessage = DomainEntityCreator.createEvidenceNonDeliveryMessage();
        createSimpleTestMessage.getTransportedMessageConfirmations()
                               .get(0).setEvidence("evidence".getBytes(StandardCharsets.UTF_8));
        boolean isEvidenceMessage = DomainModelHelper.isEvidenceTriggerMessage(createSimpleTestMessage);
        assertThat(isEvidenceMessage).isFalse();
    }

    @Test
    void testIsEvidenceTriggerMessage_shouldBeTrue() {
        DomibusConnectorMessage createSimpleTestMessage = DomainEntityCreator.createEvidenceNonDeliveryMessage();
        createSimpleTestMessage.getTransportedMessageConfirmations()
                               .get(0).setEvidence(null);
        boolean isEvidenceMessage = DomainModelHelper.isEvidenceTriggerMessage(createSimpleTestMessage);
        assertThat(isEvidenceMessage).isTrue();
    }

    @Test
    void testIsEvidenceTriggerMessageEmptyByteArray_shouldBeTrue() {
        DomibusConnectorMessage createSimpleTestMessage = DomainEntityCreator.createEvidenceNonDeliveryMessage();
        createSimpleTestMessage.getTransportedMessageConfirmations()
                               .get(0).setEvidence(new byte[0]);
        boolean isEvidenceMessage = DomainModelHelper.isEvidenceTriggerMessage(createSimpleTestMessage);
        assertThat(isEvidenceMessage).isTrue();
    }

    @Test
    void testIsEvidenceTriggerMessage_businessMsg_shouldBeFalse() {
        DomibusConnectorMessage createSimpleTestMessage = DomainEntityCreator.createSimpleTestMessage();
        boolean isEvidenceMessage = DomainModelHelper.isEvidenceTriggerMessage(createSimpleTestMessage);
        assertThat(isEvidenceMessage).isFalse();
    }

    @Test
    void switchMessageDirection() {
        DomibusConnectorMessage origMsg = DomainEntityCreator.createEpoMessage();
        origMsg.getMessageDetails().setDirection(DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);
        DomibusConnectorMessageDetails swMsgDetails =
                DomainModelHelper.switchMessageDirection(origMsg.getMessageDetails());

        swMsgDetails.getFromParty().setRole("fromRole");
        swMsgDetails.getToParty().setRole("toRole");

        DomibusConnectorMessageDetails origMsgDetails = origMsg.getMessageDetails();

        // assert direction is switched
        assertThat(swMsgDetails.getDirection()).isEqualTo(DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);
        // assert that party is switched
        assertThat(swMsgDetails.getFromParty()).isNotNull();
        assertThat(swMsgDetails.getFromParty()).isEqualToComparingOnlyGivenFields(
                origMsgDetails.getToParty(),
                "partyId",
                "partyIdType"
        );
        assertThat(swMsgDetails.getFromParty().getRoleType())
                .as("party role type of from party is always initiator")
                .isEqualTo(DomibusConnectorParty.PartyRoleType.INITIATOR);
        assertThat(swMsgDetails.getFromParty().getRole())
                .as("party role of from party is always fromRole")
                .isEqualTo("fromRole");
        assertThat(swMsgDetails.getToParty()).isNotNull();
        assertThat(swMsgDetails.getToParty())
                .isEqualToComparingOnlyGivenFields(origMsgDetails.getFromParty(), "partyId", "partyIdType");
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
