/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.controller.exception.DomibusConnectorMessageException;
import eu.domibus.connector.controller.processor.util.ConfirmationCreatorService;
import eu.domibus.connector.controller.processor.util.FindBusinessMessageByMsgId;
import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.domibus.connector.domain.enums.MessageTargetSource;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.helper.DomainModelHelper;
import eu.domibus.connector.evidences.DomibusConnectorEvidencesToolkit;
import eu.domibus.connector.lib.logging.MDC;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Processes an evidence trigger message
 * the evidence trigger only contains a refToMessageId.
 * All other AS4 attributes must be read from the business message
 */
@Component
public class EvidenceTriggerStep implements MessageProcessStep {
    private static final Logger LOGGER = LogManager.getLogger(EvidenceTriggerStep.class);
    private final FindBusinessMessageByMsgId findBusinessMessageByMsgId;
    private final ConfirmationCreatorService confirmationCreatorService;

    public EvidenceTriggerStep(FindBusinessMessageByMsgId findBusinessMessageByMsgId,
                               DomibusConnectorEvidencesToolkit evidencesToolkit,
                               ConfirmationCreatorService confirmationCreatorService) {
        this.findBusinessMessageByMsgId = findBusinessMessageByMsgId;
        this.confirmationCreatorService = confirmationCreatorService;
    }

    @Override
    @MDC(
        name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME,
        value = "EvidenceTriggerStep"
    )
    public boolean executeStep(DomibusConnectorMessage evidenceTriggerMsg) {

        // is evidence triggering allowed
        isEvidenceTriggeringAllowed(evidenceTriggerMsg);

        // only incoming evidence messages are looked up
        DomibusConnectorMessage businessMsg =
            findBusinessMessageByMsgId.findBusinessMessageByIdAndDirection(
                evidenceTriggerMsg,
                DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND
            );

        DomibusConnectorEvidenceType evidenceType = getEvidenceType(evidenceTriggerMsg);

        // create evidence
        DomibusConnectorMessageConfirmation confirmation =
            confirmationCreatorService.createConfirmation(
                evidenceType, businessMsg, DomibusConnectorRejectionReason.OTHER, ""
            );
        LOGGER.info(
            LoggingMarker.Log4jMarker.BUSINESS_LOG,
            "Successfully created evidence [{}] for evidence trigger", evidenceType
        );

        // set generated evidence into the trigger message
        // ConfirmationCreatorService.DomibusConnectorMessageConfirmationWrapper evidenceWrapper
        // = confirmation.build();
        evidenceTriggerMsg.getTransportedMessageConfirmations().getFirst()
            .setEvidence(confirmation.getEvidence());

        DomibusConnectorMessageDetails evidenceTriggerMsgDetails =
            evidenceTriggerMsg.getMessageDetails();

        // set correct action for evidence message
        evidenceTriggerMsgDetails.setAction(
            confirmationCreatorService.createEvidenceAction(confirmation.getEvidenceType())
        );
        DomibusConnectorMessageDetails businessMsgDetails = businessMsg.getMessageDetails();
        // set correct service derived from business msg
        evidenceTriggerMsgDetails.setService(businessMsgDetails.getService());

        // set correct original sender / final recipient
        evidenceTriggerMsgDetails.setOriginalSender(businessMsgDetails.getFinalRecipient());
        evidenceTriggerMsgDetails.setFinalRecipient(businessMsgDetails.getOriginalSender());
        // set correct from/to party
        evidenceTriggerMsgDetails.setFromParty(businessMsgDetails.getToParty());
        evidenceTriggerMsgDetails.setToParty(businessMsgDetails.getFromParty());

        return true;
    }

    private void isEvidenceTriggeringAllowed(DomibusConnectorMessage evidenceTriggerMsg) {
        if (!DomainModelHelper.isEvidenceTriggerMessage(evidenceTriggerMsg)) {
            throwException(evidenceTriggerMsg, "The message is not an evidence trigger message!");
        }
        MessageTargetSource source =
            evidenceTriggerMsg.getMessageDetails().getDirection().getSource();
        if (source != MessageTargetSource.BACKEND) {
            throwException(evidenceTriggerMsg, "Only backend can generate trigger messages");
        }
    }

    private void throwException(DomibusConnectorMessage evidenceTriggerMsg, String s) {
        throw new DomibusConnectorMessageException(
            evidenceTriggerMsg, EvidenceTriggerStep.class, s);
    }

    private DomibusConnectorEvidenceType getEvidenceType(
        DomibusConnectorMessage evidenceTriggerMsg) {
        if (!DomainModelHelper.isEvidenceTriggerMessage(evidenceTriggerMsg)) {
            throw new DomibusConnectorMessageException(
                evidenceTriggerMsg, EvidenceTriggerStep.class,
                "The message is not an evidence trigger msg!"
            );
        }
        DomibusConnectorMessageConfirmation msgConfirmation =
            evidenceTriggerMsg.getTransportedMessageConfirmations().getFirst();
        return msgConfirmation.getEvidenceType();
    }
}
