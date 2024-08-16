/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.domain.model.helper;

import static eu.domibus.connector.domain.model.DomibusConnectorParty.PartyRoleType.INITIATOR;
import static eu.domibus.connector.domain.model.DomibusConnectorParty.PartyRoleType.RESPONDER;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageDetailsBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorPartyBuilder;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.lang.Nullable;

/**
 * This class contains static helper methods for the domain model.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@brz.gv.at> }
 */
@UtilityClass
public class DomainModelHelper {
    public static final String ASICS_CONTAINER_IDENTIFIER = "ASIC-S";
    public static final String MESSAGE_IS_NOT_ALLOWED_TO_BE_NULL =
        "Message is not allowed to be null!";

    /**
     * Checks if the message is an evidence message.
     * <ul>
     *     <li>the message content of the message must be null
     *     {@see DomibusConnectorMessage#getMessageContent()}
     *     <li>the message must have at least one confirmation
     *     {@see DomibusConnectorMessage#getTransportedMessageConfirmations()}
     * </ul>
     *
     * @param message - the message to check
     * @return true if it is an evidence message
     */
    public static boolean isEvidenceMessage(DomibusConnectorMessage message) {
        if (message == null) {
            throw new IllegalArgumentException(MESSAGE_IS_NOT_ALLOWED_TO_BE_NULL);
        }
        return message.getMessageContent() == null
            && !message.getTransportedMessageConfirmations().isEmpty();
    }

    /**
     * Checks if the message is an evidence trigger message.
     * <ul>
     *     <li>message content of the message must be null
     *     {@see DomibusConnectorMessage#getMessageContent()}</li>
     *     <li>the message must contain exact one confirmation
     *     {@see DomibusConnectorMessage#getTransportedMessageConfirmations()}</li>
     *     <li>the confirmation must have only a confirmation type - the evidence must be empty or
     *     null: ArrayUtils.isEmpty({@see  DomibusConnectorMessageConfirmation#getEvidence()})</li>
     * </ul>
     *
     * @param message - the message to check
     * @return true if it is an evidence trigger message
     */
    public static boolean isEvidenceTriggerMessage(DomibusConnectorMessage message) {
        if (message == null) {
            throw new IllegalArgumentException(MESSAGE_IS_NOT_ALLOWED_TO_BE_NULL);
        }
        return message.getMessageContent() == null
            && message.getTransportedMessageConfirmations().size() == 1
            && ArrayUtils.isEmpty(
            message.getTransportedMessageConfirmations().getFirst().getEvidence());
    }

    /**
     * The message is a business message if it is not an evidence message see also.
     * {@link #isEvidenceMessage(DomibusConnectorMessage)}
     *
     * @param message - the message to check
     * @return true if it is a business message
     */
    public static boolean isBusinessMessage(DomibusConnectorMessage message) {
        return !isEvidenceMessage(message);
    }

    /**
     * Retrieves the evidence type of evidence message.
     *
     * @param message The evidence message to get the evidence type from.
     * @return The evidence type of the evidence message, or null if the message is null or does not
     *      have any transported message confirmations.
     */
    public static @Nullable
    DomibusConnectorEvidenceType getEvidenceTypeOfEvidenceMessage(DomibusConnectorMessage message) {
        if (message == null || message.getTransportedMessageConfirmations() == null
            || message.getTransportedMessageConfirmations().isEmpty()) {
            return null;
        }
        return message.getTransportedMessageConfirmations().getFirst().getEvidenceType();
    }

    /**
     * Checks if the message is a generate evidence message trigger the message is an evidence.
     * message trigger if the message is
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
            throw new IllegalArgumentException(MESSAGE_IS_NOT_ALLOWED_TO_BE_NULL);
        }
        if (message.getMessageDetails() == null) {
            throw new IllegalArgumentException("MessageDetails cannot be null!");
        }

        return message.getMessageDetails().getDirection()
            == DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY && isEvidenceMessage(message)
            && message.getTransportedMessageConfirmations() != null
            && message.getTransportedMessageConfirmations().size() == 1
            && message.getTransportedMessageConfirmations().getFirst().getEvidence() == null
            || message.getTransportedMessageConfirmations().getFirst().getEvidence().length == 0;
    }

    /**
     * This helper method switches the message direction by switching.
     * <ul>
     *     <li>MessageDirection {@link DomibusConnectorMessageDirection}</li>
     *     <li>FromParty with ToParty</li>
     *     <li>OriginalSender with FinalRecipient</li>
     * </ul>
     *
     * <p>When the party is switched, the party role and role type is preserved
     * so the fromParty will always have the role type of
     * {@link DomibusConnectorParty.PartyRoleType#INITIATOR}
     * and the toParty will always have the role type of
     * {@link DomibusConnectorParty.PartyRoleType#RESPONDER}
     *
     * @param messageDetails The {@link DomibusConnectorMessageDetails} to switch the message
     *                       direction for.
     * @return A new {@link DomibusConnectorMessageDetails} object with the switched message
     *      direction.
     */
    public static DomibusConnectorMessageDetails switchMessageDirection(
        DomibusConnectorMessageDetails messageDetails) {
        DomibusConnectorMessageDetails details = DomibusConnectorMessageDetailsBuilder.create()
            .copyPropertiesFrom(messageDetails)
            .build();

        // switching party, but keep Role and RoleType
        DomibusConnectorParty newToParty =
            DomibusConnectorPartyBuilder.createBuilder().copyPropertiesFrom(details.getFromParty())
                .build();
        newToParty.setRoleType(RESPONDER);
        newToParty.setRole(details.getToParty().getRole());
        // switching party, but keep Role and RoleType
        DomibusConnectorParty newFromParty =
            DomibusConnectorPartyBuilder.createBuilder().copyPropertiesFrom(details.getToParty())
                .build();
        newFromParty.setRoleType(INITIATOR);
        newFromParty.setRole(details.getFromParty().getRole());

        var originalDirection = details.getDirection();
        var newFinalRecipient = details.getOriginalSender();
        var newOriginalSender = details.getFinalRecipient();

        details.setDirection(
            DomibusConnectorMessageDirection.fromMessageTargetSource(
                originalDirection.getTarget(),
                originalDirection.getSource()
            ));
        details.setOriginalSender(newOriginalSender);
        details.setFinalRecipient(newFinalRecipient);
        details.setFromParty(newFromParty);
        details.setToParty(newToParty);
        return details;
    }
}
