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
                throw new DomibusConnectorControllerException("Illegal Evidence type " + type + "! No Action found!");
        }
    }

    public DomibusConnectorMessageConfirmation createConfirmation(
            DomibusConnectorEvidenceType evidenceType,
            DomibusConnectorMessage businessMsg,
            DomibusConnectorRejectionReason reason,
            String details) {
        return evidencesToolkit.createEvidence(evidenceType, businessMsg, reason, details);
    }

    public DomibusConnectorMessageConfirmation createNonDelivery(
            DomibusConnectorMessage originalMessage, DomibusConnectorRejectionReason deliveryEvidenceTimeout) {
        return evidencesToolkit.createEvidence(
                DomibusConnectorEvidenceType.NON_DELIVERY,
                originalMessage, deliveryEvidenceTimeout,
                deliveryEvidenceTimeout.getReasonText()
        );
    }

    public DomibusConnectorMessageConfirmation createNonRetrieval(
            DomibusConnectorMessage originalMessage,
            DomibusConnectorRejectionReason deliveryEvidenceTimeout) {
        return evidencesToolkit.createEvidence(
                DomibusConnectorEvidenceType.NON_RETRIEVAL,
                originalMessage,
                deliveryEvidenceTimeout,
                deliveryEvidenceTimeout.getReasonText()
        );
    }

    public DomibusConnectorMessageConfirmation createRelayRemmdFailure(
            DomibusConnectorMessage originalMessage, DomibusConnectorRejectionReason deliveryEvidenceTimeout) {
        return evidencesToolkit.createEvidence(
                DomibusConnectorEvidenceType.RELAY_REMMD_FAILURE,
                originalMessage,
                deliveryEvidenceTimeout,
                deliveryEvidenceTimeout.getReasonText()
        );
    }

    public DomibusConnectorMessageConfirmation createDelivery(DomibusConnectorMessage originalMessage) {
        return evidencesToolkit.createEvidence(
                DomibusConnectorEvidenceType.DELIVERY, originalMessage, null, null
        );
    }
}
