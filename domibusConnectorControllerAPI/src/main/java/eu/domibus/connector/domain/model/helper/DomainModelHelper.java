package eu.domibus.connector.domain.model.helper;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageDetailsBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorPartyBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.lang.Nullable;

import static eu.domibus.connector.domain.model.DomibusConnectorParty.PartyRoleType.INITIATOR;
import static eu.domibus.connector.domain.model.DomibusConnectorParty.PartyRoleType.RESPONDER;


/**
 * This class contains static helper methods
 * for the domain model
 *
 * @author {@literal Stephan Spindler <stephan.spindler@brz.gv.at> }
 */
public class DomainModelHelper {
    public static final String ASICS_CONTAINER_IDENTIFIER = "ASIC-S";

    /**
     * Checks if the message is an evidence message
     * <ul>
     *     <li>message content of the message must be null {@link DomibusConnectorMessage#getMessageContent()}</li>
     *     <li>the message must have at least one confirmation
     *     {@link DomibusConnectorMessage#getTransportedMessageConfirmations()}</li>
     * </ul>
     *
     * @param message - the message to check
     * @return true if it is an evidence message
     */
    public static boolean isEvidenceMessage(DomibusConnectorMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Message is not allowed to be null!");
        }
        return message.getMessageContent() == null && message.getTransportedMessageConfirmations().size() > 0;
    }

    /**
     * Checks if the message is an evidence trigger message
     * <ul>
     *     <li>message content of the message must be null {@link DomibusConnectorMessage#getMessageContent()}</li>
     *     <li>the message must contain exact one confirmation
     *     {@link DomibusConnectorMessage#getTransportedMessageConfirmations()}</li>
     *     <li>the confirmation must have only a confirmation type - the evidence must be empty or null: ArrayUtils
     *     .isEmpty({@link DomibusConnectorMessageConfirmation#getEvidence()})</li>
     * </ul>
     *
     * @param message - the message to check
     * @return true if it is a evidence trigger message
     */
    public static boolean isEvidenceTriggerMessage(DomibusConnectorMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Message is not allowed to be null!");
        }
        return message.getMessageContent() == null && message.getTransportedMessageConfirmations().size() == 1
                && ArrayUtils.isEmpty(message.getTransportedMessageConfirmations().get(0).getEvidence());
    }

    /**
     * The message is a businesss message if it is not an evidence message
     * see also {@link #isEvidenceMessage(DomibusConnectorMessage)}
     *
     * @param message - the message to check
     * @return true if it is a business message
     */
    public static boolean isBusinessMessage(DomibusConnectorMessage message) {
        return !isEvidenceMessage(message);
    }


    /**
     * @param message the message
     * @return the evidence type of the first confirmation of the message
     */
    public static @Nullable
    DomibusConnectorEvidenceType getEvidenceTypeOfEvidenceMessage(DomibusConnectorMessage message) {
        if (message == null || message.getTransportedMessageConfirmations() == null ||
                message.getTransportedMessageConfirmations().size() == 0) {
            return null;
        }
        return message.getTransportedMessageConfirmations().get(0).getEvidenceType();
    }

    /**
     * Checks if the message is a generate evidence message trigger
     * the message is a evidence message trigger if the message is
     * <ul>
     *     <li>going from backend to gateway</li>
     *     <li>is a evidence message {@link #isEvidenceMessage(DomibusConnectorMessage)}</li>
     *     <li>the xml of the evidence has size 0</li>
     * </ul>
     *
     * @param message - the message to check
     * @return true if it is a trigger message!
     */
    public static boolean
    isEvidenceMessageTrigger(DomibusConnectorMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Message is not allowed to be null!");
        }
        if (message.getMessageDetails() == null) {
            throw new IllegalArgumentException("MessageDetails cannot be null!");
        }

        return message.getMessageDetails().getDirection() == DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY &&
                isEvidenceMessage(message)
                && message.getTransportedMessageConfirmations() != null
                && message.getTransportedMessageConfirmations().size() == 1
                && message.getTransportedMessageConfirmations().get(0).getEvidence() == null
                || message.getTransportedMessageConfirmations().get(0).getEvidence().length == 0;
    }

    /**
     * This helper method switches the message direction by switching
     * <ul>
     *     <li>MessageDirection {@link DomibusConnectorMessageDirection}</li>
     *     <li>FromParty with ToParty</li>
     *     <li>OriginalSender with FinalRecipient</li>
     * </ul>
     * <p>
     * When the party is switched, the party role and role type is preserved
     * so the fromParty will always have the role type of {@link DomibusConnectorParty.PartyRoleType#INITIATOR}
     * and the toParty will always have the role type of {@link DomibusConnectorParty.PartyRoleType#RESPONDER}
     *
     * @param messageDetails
     * @return
     */
    public static DomibusConnectorMessageDetails switchMessageDirection(DomibusConnectorMessageDetails messageDetails) {
        DomibusConnectorMessageDetails details = DomibusConnectorMessageDetailsBuilder
                .create()
                .copyPropertiesFrom(messageDetails)
                .build();
        DomibusConnectorMessageDirection originalDirection = details.getDirection();
        String newFinalRecipient = details.getOriginalSender();
        String newOriginalSender = details.getFinalRecipient();

        // switching party, but keep Role and RoleType
        DomibusConnectorParty newToParty = DomibusConnectorPartyBuilder
                .createBuilder()
                .copyPropertiesFrom(details.getFromParty())
                .build();
        newToParty.setRoleType(RESPONDER);
        newToParty.setRole(details.getToParty().getRole());
        // switching party, but keep Role and RoleType
        DomibusConnectorParty newFromParty = DomibusConnectorPartyBuilder
                .createBuilder()
                .copyPropertiesFrom(details.getToParty())
                .build();
        newFromParty.setRoleType(INITIATOR);
        newFromParty.setRole(details.getFromParty().getRole());

        details.setDirection(
                DomibusConnectorMessageDirection.fromMessageTargetSource(
                        originalDirection.getTarget(),
                        originalDirection.getSource()
                )
        );
        details.setOriginalSender(newOriginalSender);
        details.setFinalRecipient(newFinalRecipient);
        details.setFromParty(newFromParty);
        details.setToParty(newToParty);
        return details;
    }
}
