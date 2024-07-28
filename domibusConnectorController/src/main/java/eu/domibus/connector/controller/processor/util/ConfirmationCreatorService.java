/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.processor.util;

import eu.domibus.connector.common.service.ConfigurationPropertyManagerService;
import eu.domibus.connector.controller.exception.DomibusConnectorControllerException;
import eu.domibus.connector.controller.service.DomibusConnectorMessageIdGenerator;
import eu.domibus.connector.domain.configuration.EvidenceActionServiceConfigurationProperties;
import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.evidences.DomibusConnectorEvidencesToolkit;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import eu.domibus.connector.persistence.service.DomibusConnectorEvidencePersistenceService;
import org.springframework.stereotype.Component;

/**
 * The ConfirmationCreatorService class provides methods for creating various types of confirmations
 * and actions for a given evidence type.
 * It uses the DomibusConnectorEvidencesToolkit and ConfigurationPropertyManagerService
 * to generate the confirmations and actions.
 */
@Component
public class ConfirmationCreatorService {
    private final DomibusConnectorEvidencesToolkit evidencesToolkit;
    private final ConfigurationPropertyManagerService configurationPropertyLoaderService;

    public ConfirmationCreatorService(
        DomibusConnectorEvidencesToolkit evidencesToolkit,
        DomibusConnectorEvidencePersistenceService evidencePersistenceService,
        DomibusConnectorMessageIdGenerator messageIdGenerator,
        ConfigurationPropertyManagerService configurationPropertyLoaderService,
        DCMessagePersistenceService messagePersistenceService) {
        this.evidencesToolkit = evidencesToolkit;
        this.configurationPropertyLoaderService = configurationPropertyLoaderService;
    }

    /**
     * Creates a DomibusConnectorAction based on the given evidence type.
     *
     * @param type The evidence type.
     * @return The created DomibusConnectorAction.
     * @throws DomibusConnectorControllerException If an illegal evidence type is provided and
     *                                             no action is found.
     */
    public DomibusConnectorAction createEvidenceAction(DomibusConnectorEvidenceType type)
        throws DomibusConnectorControllerException {
        EvidenceActionServiceConfigurationProperties evidenceActionServiceConfigurationProperties =
            configurationPropertyLoaderService.loadConfiguration(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
                EvidenceActionServiceConfigurationProperties.class
            );

        switch (type) {
            case DELIVERY:
                return evidenceActionServiceConfigurationProperties
                    .getDelivery().getConnectorAction();
            case NON_DELIVERY:
                return evidenceActionServiceConfigurationProperties
                    .getNonDelivery().getConnectorAction();
            case RETRIEVAL:
                return evidenceActionServiceConfigurationProperties
                    .getRetrieval().getConnectorAction();
            case NON_RETRIEVAL:
                return evidenceActionServiceConfigurationProperties
                    .getNonRetrieval().getConnectorAction();
            case RELAY_REMMD_FAILURE:
                return evidenceActionServiceConfigurationProperties
                    .getRelayREMMDFailure().getConnectorAction();
            case RELAY_REMMD_REJECTION:
                return evidenceActionServiceConfigurationProperties
                    .getRelayREEMDRejection().getConnectorAction();
            case RELAY_REMMD_ACCEPTANCE:
                return evidenceActionServiceConfigurationProperties
                    .getRelayREEMDAcceptance().getConnectorAction();
            case SUBMISSION_ACCEPTANCE:
                return evidenceActionServiceConfigurationProperties
                    .getSubmissionAcceptance().getConnectorAction();
            case SUBMISSION_REJECTION:
                return evidenceActionServiceConfigurationProperties
                    .getSubmissionRejection().getConnectorAction();
            default:
                throw new DomibusConnectorControllerException(
                    "Illegal Evidence type " + type + "! No Action found!");
        }
    }

    public DomibusConnectorMessageConfirmation createConfirmation(
        DomibusConnectorEvidenceType evidenceType, DomibusConnectorMessage businessMsg,
        DomibusConnectorRejectionReason reason, String details) {
        return evidencesToolkit.createEvidence(evidenceType, businessMsg, reason, details);
    }

    /**
     * Creates a non-delivery evidence for a given original message and delivery evidence
     * timeout reason.
     *
     * @param originalMessage         The original message for which the non-delivery evidence is
     *                                created.
     * @param deliveryEvidenceTimeout The reason for the delivery evidence timeout.
     * @return The created DomibusConnectorMessageConfirmation object representing
     *      the non-delivery evidence.
     */
    public DomibusConnectorMessageConfirmation createNonDelivery(
        DomibusConnectorMessage originalMessage,
        DomibusConnectorRejectionReason deliveryEvidenceTimeout) {
        return evidencesToolkit.createEvidence(
            DomibusConnectorEvidenceType.NON_DELIVERY, originalMessage, deliveryEvidenceTimeout,
            deliveryEvidenceTimeout.getReasonText()
        );
    }

    /**
     * Creates a non-retrieval evidence for a given original message and delivery evidence
     * timeout reason.
     *
     * @param originalMessage         The original message for which the non-retrieval evidence
     *                                is created.
     * @param deliveryEvidenceTimeout The reason for the delivery evidence timeout.
     * @return The created DomibusConnectorMessageConfirmation object representing the
     *      non-retrieval evidence.
     */
    public DomibusConnectorMessageConfirmation createNonRetrieval(
        DomibusConnectorMessage originalMessage,
        DomibusConnectorRejectionReason deliveryEvidenceTimeout) {
        return evidencesToolkit.createEvidence(
            DomibusConnectorEvidenceType.NON_RETRIEVAL, originalMessage, deliveryEvidenceTimeout,
            deliveryEvidenceTimeout.getReasonText()
        );
    }

    /**
     * Creates a DomibusConnectorMessageConfirmation object representing a relay REMMD
     * failure evidence.
     *
     * @param originalMessage         The original message for which the relay REMMD
     *                                failure evidence is created.
     * @param deliveryEvidenceTimeout The reason for the delivery evidence timeout.
     * @return The created DomibusConnectorMessageConfirmation object representing the
     *      relay REMMD failure evidence.
     */
    public DomibusConnectorMessageConfirmation createRelayRemmdFailure(
        DomibusConnectorMessage originalMessage,
        DomibusConnectorRejectionReason deliveryEvidenceTimeout) {
        return evidencesToolkit.createEvidence(
            DomibusConnectorEvidenceType.RELAY_REMMD_FAILURE, originalMessage,
            deliveryEvidenceTimeout, deliveryEvidenceTimeout.getReasonText()
        );
    }

    public DomibusConnectorMessageConfirmation createDelivery(
        DomibusConnectorMessage originalMessage) {
        return evidencesToolkit.createEvidence(
            DomibusConnectorEvidenceType.DELIVERY, originalMessage, null, null);
    }
}
