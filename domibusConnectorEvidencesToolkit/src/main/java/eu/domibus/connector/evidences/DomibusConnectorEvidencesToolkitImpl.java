/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.evidences;

import eu.domibus.connector.common.annotations.BusinessDomainScoped;
import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.evidences.exception.DomibusConnectorEvidencesToolkitException;
import eu.domibus.connector.evidences.spring.EvidencesToolkitConfigurationProperties;
import eu.ecodex.evidences.EvidenceBuilder;
import eu.ecodex.evidences.exception.ECodexEvidenceBuilderException;
import eu.ecodex.evidences.types.ECodexMessageDetails;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.bouncycastle.util.encoders.Hex;
import org.etsi.uri._02640.v2.EventReasonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of the DomibusConnectorEvidencesToolkit interface to create eCodex Evidence
 * Messages.
 */
@SuppressWarnings("squid:S1135")
@BusinessDomainScoped
@Component
public class DomibusConnectorEvidencesToolkitImpl implements DomibusConnectorEvidencesToolkit {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DomibusConnectorEvidencesToolkitImpl.class);
    @Autowired
    private EvidenceBuilder evidenceBuilder;
    @Autowired
    private HashValueBuilder hashValueBuilder;
    @Autowired
    EvidencesToolkitConfigurationProperties evidencesToolkitConfigurationProperties;

    @Override
    public DomibusConnectorMessageConfirmation createEvidence(
        DomibusConnectorEvidenceType type, DomibusConnectorMessage message,
        DomibusConnectorRejectionReason rejectionReason, String details)
        throws DomibusConnectorEvidencesToolkitException {
        LOGGER.debug("#createEvidence: [{}] for message [{}]", type, message);
        LOGGER.trace(
            "#createEvidence: message contains following evidences: [{}]",
            message.getRelatedMessageConfirmations()
        );
        byte[] evidence = null;
        switch (type) {
            case SUBMISSION_ACCEPTANCE:
                evidence = createSubmissionAcceptance(message);
                break;
            case SUBMISSION_REJECTION:
                evidence = createSubmissionRejection(rejectionReason, message, details);
                break;
            case DELIVERY:
                evidence = createDeliveryEvidence(message);
                break;
            case NON_DELIVERY:
                evidence = createNonDeliveryEvidence(rejectionReason, message, details);
                break;
            case RETRIEVAL:
                evidence = createRetrievalEvidence(message);
                break;
            case NON_RETRIEVAL:
                evidence = createNonRetrievalEvidence(rejectionReason, message, details);
                break;
            case RELAY_REMMD_ACCEPTANCE:
                evidence = createRelayREMMDAcceptance(message);
                break;
            case RELAY_REMMD_FAILURE:
                evidence = createRelayREMMDFailure(rejectionReason, message, details);
                break;
            case RELAY_REMMD_REJECTION:
                evidence = createRelayREMMDRejection(rejectionReason, message, details);
                break;
            default:
                break;
        }

        if (evidence == null) {
            // fail if the evidence couldn't be created! So illegal null evidences aren't used in
            // the connector!
            // TODO: make this error more transparent
            throw new DomibusConnectorEvidencesToolkitException(
                "Evidence could not be created by evidenceToolkit impl");
        }

        return buildConfirmation(type, evidence);
    }

    private String checkPDFandBuildHashValue(DomibusConnectorMessage message)
        throws DomibusConnectorEvidencesToolkitException {
        String hashValue = null;
        if (ArrayUtils.isEmpty(message.getMessageContent().getXmlContent())) {
            DomibusConnectorAction action = message.getMessageDetails().getAction();
            if (action == null) {
                throw new DomibusConnectorEvidencesToolkitException("Action still null!");
            }
            // if (action.isDocumentRequired()) {
            //     throw new DomibusConnectorEvidencesToolkitException(
            //             "There is no document in the message though the Action "
            //             + action.getAction() + " requires one!");
            // }
        } else {
            try {
                hashValue = hashValueBuilder.buildHashValueAsString(
                    message.getMessageContent().getXmlContent());
            } catch (Exception e) {
                throw new DomibusConnectorEvidencesToolkitException(
                    "Could not build hash code though the PDF is not empty!",
                    e
                );
            }
        }
        return hashValue;
    }

    private byte[] createSubmissionAcceptance(DomibusConnectorMessage message)
        throws DomibusConnectorEvidencesToolkitException {
        String hash = checkPDFandBuildHashValue(message);

        return createSubmissionAcceptanceRejection(true, null, message, hash);
    }

    private byte[] createSubmissionRejection(
        DomibusConnectorRejectionReason rejectionReason, DomibusConnectorMessage message,
        String errorDetails
    ) throws DomibusConnectorEvidencesToolkitException {
        if (rejectionReason == null) {
            throw new DomibusConnectorEvidencesToolkitException(
                "in case of a rejection the rejectionReason may not be null!");
        }

        var event = new EventReasonType();
        event.setCode(rejectionReason.toString());
        event.setDetails(errorDetails);

        var hash = checkPDFandBuildHashValue(message);
        return createSubmissionAcceptanceRejection(false, event, message, hash);
    }

    private String createEvidencesToolkitExceptionMessage(
        DomibusConnectorEvidenceType evidenceType1, DomibusConnectorEvidenceType evidenceType2) {
        var message = new StrBuilder();
        message.append("Message contains no evidence of type ");
        message.append(evidenceType1.name());
        message.append("! No evidence of type ");
        message.append(evidenceType2);
        message.append(" can be created!");

        return message.toString();
    }

    private byte[] createRelayREMMDAcceptance(DomibusConnectorMessage message)
        throws DomibusConnectorEvidencesToolkitException {
        DomibusConnectorMessageConfirmation prevConfirmation =
            findConfirmation(DomibusConnectorEvidenceType.SUBMISSION_ACCEPTANCE, message);

        if (prevConfirmation == null) {
            throw new DomibusConnectorEvidencesToolkitException(
                createEvidencesToolkitExceptionMessage(
                    DomibusConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                    DomibusConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE
                )
            );
        }

        return createRelayREMMDAcceptanceRejection(true, null, prevConfirmation.getEvidence());
    }

    private byte[] createRelayREMMDRejection(
        DomibusConnectorRejectionReason rejectionReason, DomibusConnectorMessage message,
        String errorDetails) throws DomibusConnectorEvidencesToolkitException {
        if (rejectionReason == null) {
            throw new DomibusConnectorEvidencesToolkitException(
                "in case of a rejection the rejectionReason may not be null!");
        }

        var event = new EventReasonType();
        event.setCode(rejectionReason.toString());
        event.setDetails(errorDetails);

        DomibusConnectorMessageConfirmation prevConfirmation =
            findConfirmation(DomibusConnectorEvidenceType.SUBMISSION_ACCEPTANCE, message);

        if (prevConfirmation == null) {
            throw new DomibusConnectorEvidencesToolkitException(
                createEvidencesToolkitExceptionMessage(
                    DomibusConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                    DomibusConnectorEvidenceType.RELAY_REMMD_REJECTION
                )
            );
        }

        return createRelayREMMDAcceptanceRejection(false, event, prevConfirmation.getEvidence());
    }

    private byte[] createDeliveryEvidence(DomibusConnectorMessage message)
        throws DomibusConnectorEvidencesToolkitException {
        DomibusConnectorMessageConfirmation prevConfirmation =
            findConfirmation(DomibusConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE, message);

        if (prevConfirmation == null) {
            throw new DomibusConnectorEvidencesToolkitException(
                createEvidencesToolkitExceptionMessage(
                    DomibusConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE,
                    DomibusConnectorEvidenceType.DELIVERY
                )
            );
        }

        return createDeliveryNonDeliveryEvidence(true, null, prevConfirmation.getEvidence());
    }

    private byte[] createNonDeliveryEvidence(
        DomibusConnectorRejectionReason rejectionReason, DomibusConnectorMessage message,
        String errorDetails) throws DomibusConnectorEvidencesToolkitException {

        if (rejectionReason == null) {
            throw new DomibusConnectorEvidencesToolkitException(
                "in case of a NonDelivery the rejectionReason may not be null!");
        }

        var event = new EventReasonType();
        event.setCode(rejectionReason.toString());
        event.setDetails(errorDetails);

        DomibusConnectorMessageConfirmation prevConfirmation =
            findConfirmation(DomibusConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE, message);

        if (prevConfirmation == null) {
            throw new DomibusConnectorEvidencesToolkitException(
                createEvidencesToolkitExceptionMessage(
                    DomibusConnectorEvidenceType.RELAY_REMMD_ACCEPTANCE,
                    DomibusConnectorEvidenceType.NON_DELIVERY
                )
            );
        }

        return createDeliveryNonDeliveryEvidence(false, event, prevConfirmation.getEvidence());
    }

    private byte[] createRetrievalEvidence(DomibusConnectorMessage message)
        throws DomibusConnectorEvidencesToolkitException {
        DomibusConnectorMessageConfirmation prevConfirmation =
            findConfirmation(DomibusConnectorEvidenceType.DELIVERY, message);

        if (prevConfirmation == null) {
            throw new DomibusConnectorEvidencesToolkitException(
                createEvidencesToolkitExceptionMessage(
                    DomibusConnectorEvidenceType.DELIVERY,
                    DomibusConnectorEvidenceType.RETRIEVAL
                )
            );
        }

        return createRetrievalNonRetrievalEvidence(true, null, prevConfirmation.getEvidence());
    }

    private byte[] createNonRetrievalEvidence(
        DomibusConnectorRejectionReason rejectionReason, DomibusConnectorMessage message,
        String errorDetails) throws DomibusConnectorEvidencesToolkitException {

        if (rejectionReason == null) {
            throw new DomibusConnectorEvidencesToolkitException(
                "in case of a NonRetrieval the rejectionReason may not be null!");
        }

        var event = new EventReasonType();
        event.setCode(rejectionReason.toString());
        event.setDetails(errorDetails);

        DomibusConnectorMessageConfirmation prevConfirmation =
            findConfirmation(DomibusConnectorEvidenceType.DELIVERY, message);

        if (prevConfirmation == null) {
            throw new DomibusConnectorEvidencesToolkitException(
                createEvidencesToolkitExceptionMessage(
                    DomibusConnectorEvidenceType.DELIVERY,
                    DomibusConnectorEvidenceType.NON_RETRIEVAL
                )
            );
        }

        return createRetrievalNonRetrievalEvidence(false, event, prevConfirmation.getEvidence());
    }

    private DomibusConnectorMessageConfirmation findConfirmation(
        DomibusConnectorEvidenceType evidenceType, DomibusConnectorMessage message) {
        if (message.getRelatedMessageConfirmations() != null) {
            for (DomibusConnectorMessageConfirmation confirmation
                : message.getRelatedMessageConfirmations()) {
                if (confirmation.getEvidenceType().equals(evidenceType)) {
                    return confirmation;
                }
            }
        }
        return null;
    }

    private DomibusConnectorMessageConfirmation buildConfirmation(
        DomibusConnectorEvidenceType evidenceType, byte[] evidence) {
        var confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidenceType(evidenceType);
        confirmation.setEvidence(evidence);
        return confirmation;
    }

    private byte[] createRetrievalNonRetrievalEvidence(
        boolean isRetrieval, EventReasonType eventReason,
        byte[] previousEvidence) throws DomibusConnectorEvidencesToolkitException {
        var evidenceIssuerDetails = buildEDeliveryDetails();

        try {
            return evidenceBuilder.createRetrievalNonRetrievalByRecipient(isRetrieval, eventReason,
                                                                          evidenceIssuerDetails,
                                                                          previousEvidence
            );
        } catch (ECodexEvidenceBuilderException e) {
            throw new DomibusConnectorEvidencesToolkitException(e);
        }
    }

    private byte[] createDeliveryNonDeliveryEvidence(
        boolean isDelivery, EventReasonType eventReason,
        byte[] previousEvidence) throws DomibusConnectorEvidencesToolkitException {
        var evidenceIssuerDetails = buildEDeliveryDetails();

        try {
            return evidenceBuilder.createDeliveryNonDeliveryToRecipient(
                isDelivery, eventReason, evidenceIssuerDetails, previousEvidence
            );
        } catch (ECodexEvidenceBuilderException e) {
            throw new DomibusConnectorEvidencesToolkitException(e);
        }
    }

    private byte[] createRelayREMMDAcceptanceRejection(
        boolean isAcceptance, EventReasonType eventReason,
        byte[] previousEvidence) throws DomibusConnectorEvidencesToolkitException {
        var evidenceIssuerDetails = buildEDeliveryDetails();

        try {
            return evidenceBuilder.createRelayREMMDAcceptanceRejection(
                isAcceptance, eventReason, evidenceIssuerDetails, previousEvidence
            );
        } catch (ECodexEvidenceBuilderException e) {
            throw new DomibusConnectorEvidencesToolkitException(e);
        }
    }

    private byte[] createRelayREMMDFailure(
        DomibusConnectorRejectionReason rejectionReason, DomibusConnectorMessage message,
        String errorDetails) throws DomibusConnectorEvidencesToolkitException {

        if (rejectionReason == null) {
            throw new DomibusConnectorEvidencesToolkitException(
                "in case of a failure the rejectionReason may not be null!");
        }

        var event = new EventReasonType();
        event.setCode(rejectionReason.toString());
        event.setDetails(errorDetails);

        DomibusConnectorMessageConfirmation prevConfirmation =
            findConfirmation(DomibusConnectorEvidenceType.SUBMISSION_ACCEPTANCE, message);

        if (prevConfirmation == null) {
            throw new DomibusConnectorEvidencesToolkitException(
                createEvidencesToolkitExceptionMessage(
                    DomibusConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                    DomibusConnectorEvidenceType.RELAY_REMMD_FAILURE
                )
            );
        }

        return createRelayREMMDFailure(event, prevConfirmation.getEvidence());
    }

    private byte[] createRelayREMMDFailure(EventReasonType eventReason, byte[] previousEvidence)
        throws DomibusConnectorEvidencesToolkitException {
        var evidenceIssuerDetails = buildEDeliveryDetails();

        try {
            return evidenceBuilder.createRelayREMMDFailure(
                eventReason, evidenceIssuerDetails, previousEvidence);
        } catch (ECodexEvidenceBuilderException e) {
            throw new DomibusConnectorEvidencesToolkitException(e);
        }
    }

    private byte[] createSubmissionAcceptanceRejection(
        boolean isAcceptance, EventReasonType eventReason,
        DomibusConnectorMessage message, String hash)
        throws DomibusConnectorEvidencesToolkitException {
        var evidenceIssuerDetails = buildEDeliveryDetails();

        String nationalMessageId = message.getMessageDetails().getBackendMessageId();

        String senderAddress = message.getMessageDetails().getOriginalSender();

        String recipientAddress = message.getMessageDetails().getFinalRecipient();

        ECodexMessageDetails messageDetails;
        try {
            messageDetails =
                buildMessageDetails(nationalMessageId, senderAddress, recipientAddress, hash);
        } catch (DomibusConnectorEvidencesToolkitException e) {
            throw e;
        }

        try {
            return evidenceBuilder.createSubmissionAcceptanceRejection(
                isAcceptance, eventReason, evidenceIssuerDetails, messageDetails
            );
        } catch (ECodexEvidenceBuilderException e) {
            throw new DomibusConnectorEvidencesToolkitException(e);
        }
    }

    private EDeliveryDetails buildEDeliveryDetails() {
        var detail = new EDeliveryDetail();
        var homePartyConfigurationProperties =
            evidencesToolkitConfigurationProperties.getIssuerInfo().getAs4Party();

        var server = new EDeliveryDetail.Server();
        server.setGatewayName(homePartyConfigurationProperties.getName());
        server.setGatewayAddress(homePartyConfigurationProperties.getEndpointAddress());
        detail.setServer(server);

        var postalAddress = getPostalAdress();
        detail.setPostalAdress(postalAddress);

        return new EDeliveryDetails(detail);
    }

    private EDeliveryDetail.PostalAdress getPostalAdress() {
        var postalAdressConfigurationProperties =
            evidencesToolkitConfigurationProperties.getIssuerInfo().getPostalAddress();

        var postalAddress = new EDeliveryDetail.PostalAdress();
        postalAddress.setStreetAddress(postalAdressConfigurationProperties.getStreet());
        postalAddress.setLocality(postalAdressConfigurationProperties.getLocality());
        postalAddress.setPostalCode(postalAdressConfigurationProperties.getZipCode());
        postalAddress.setCountry(postalAdressConfigurationProperties.getCountry());
        return postalAddress;
    }

    private ECodexMessageDetails buildMessageDetails(
        String nationalMessageId, String senderAddress,
        String recipientAddress, String hash) throws DomibusConnectorEvidencesToolkitException {
        var messageDetails = new ECodexMessageDetails();
        LOGGER.debug(
            "#buildMessageDetails with nationalMessageId [{}], senderAddress [{}], "
                + "recipientAddress [{}], hash [{}]",
            nationalMessageId, senderAddress, recipientAddress, hash
        );

        messageDetails.setHashAlgorithm(hashValueBuilder.getAlgorithm());
        if (hash != null) {
            messageDetails.setHashValue(Hex.decode(hash));
        }

        if (nationalMessageId == null || nationalMessageId.isEmpty()) {
            throw new DomibusConnectorEvidencesToolkitException(
                "the nationalMessageId may not be null for building a submission evidence!"
            );
        }
        if (recipientAddress == null || recipientAddress.isEmpty()) {
            throw new DomibusConnectorEvidencesToolkitException(
                "the recipientAddress may not be null for building a submission evidence!"
            );
        }
        if (senderAddress == null || senderAddress.isEmpty()) {
            throw new DomibusConnectorEvidencesToolkitException(
                "the senderAddress may not be null for building a submission evidence!"
            );
        }
        messageDetails.setNationalMessageId(nationalMessageId);
        messageDetails.setRecipientAddress(recipientAddress);
        messageDetails.setSenderAddress(senderAddress);
        return messageDetails;
    }
}
