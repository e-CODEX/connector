/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.evidences;

import eu.ecodex.evidences.exception.ECodexEvidenceBuilderException;
import eu.ecodex.evidences.types.ECodexMessageDetails;
import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import org.etsi.uri._02640.v2.EventReasonType;

/**
 * Interface with methods for building REM:Evidences in e-CODEX context. All evidences created by
 * this Builder will be signed with an enveloped signature. Important: The returned byte Array
 * represents a marshalled xml file and should be validated if needed. After unmarshalling the
 * content of this file the signature will most likely break. So make sure that you validate the
 * file before.
 *
 * @author muell16
 */
public interface EvidenceBuilder {
    /**
     * Method for building the first Evidence and sign it with an enveloped signature.
     *
     * @param isAcceptance          EventCode ("http:uri.etsi.org/02640/Event#Acceptance",
     *                              "http:uri.etsi.org/02640/Event#Rejection") of the evidence.
     * @param eventReason           List of Reasons for an Error. Ignored when isAcceptance == true.
     *                              Allowed eventReasons are predefined by large scale project
     *                              SPOCS.
     * @param evidenceIssuerDetails Details of the connector creating this evidence
     * @param messageDetails        Details of the message (messageId(national), messageId(ebMS),
     *                              Hash of the original message and used hash algorithm) and sender
     *                              + recipient
     * @return signed SubmissionAcceptanceRejection - Evidence as byte array.
     * @throws ECodexEvidenceBuilderException If an error occurs during the evidence creation.
     */
    byte[] createSubmissionAcceptanceRejection(
        boolean isAcceptance, REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails,
        ECodexMessageDetails messageDetails) throws ECodexEvidenceBuilderException;

    /**
     * Method for building the first Evidence and sign it with an enveloped signature.
     *
     * @param isAcceptance          EventCode ("http:uri.etsi.org/02640/Event#Acceptance",
     *                              "http:uri.etsi.org/02640/Event#Rejection") of the evidence.
     * @param eventReason           List of Reasons for an Error. Ignored when isAcceptance == true
     * @param evidenceIssuerDetails Details of the connector creating this evidence
     * @param messageDetails        Details of the message (messageId(national), messageId(ebMS),
     *                              Hash of the original message and used hash algorithm) and sender
     *                              + recipient
     * @return signed SubmissionAcceptanceRejection - Evidence as byte array.
     * @throws ECodexEvidenceBuilderException If an error occurs during the evidence creation.
     */
    byte[] createSubmissionAcceptanceRejection(
        boolean isAcceptance, EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails,
        ECodexMessageDetails messageDetails) throws ECodexEvidenceBuilderException;

    /**
     * Method for building the second evidence from the first one and sign it with an enveloped
     * signature.
     *
     * @param isAcceptance          EventCode ("http:uri.etsi.org/02640/Event#Acceptance",
     *                              "http:uri.etsi.org/02640/Event#Rejection") of the evidence.
     * @param eventReason           List of Reasons for an Error. Ignored when isAcceptance == true.
     *                              Allowed eventReasons are predefined by large scale project
     *                              SPOCS.
     * @param evidenceIssuerDetails Details of the connector creating this evidence
     * @param previousEvidence      A SubmissionAcceptanceRejection - Evidence
     * @return signed RelayREMMDAcceptanceRejection - Evidence as byte array.
     * @throws ECodexEvidenceBuilderException If an error occurs during the evidence creation.
     */
    byte[] createRelayREMMDAcceptanceRejection(
        boolean isAcceptance, REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidence) throws ECodexEvidenceBuilderException;

    /**
     * Method for building the second evidence from the first one and sign it with an enveloped
     * signature.
     *
     * @param isAcceptance          EventCode ("http:uri.etsi.org/02640/Event#Acceptance",
     *                              "http:uri.etsi.org/02640/Event#Rejection") of the evidence.
     * @param eventReason           List of Reasons for an Error. Ignored when isAcceptance == true
     * @param evidenceIssuerDetails Details of the connector creating this evidence
     * @param previousEvidence      A SubmissionAcceptanceRejection - Evidence
     * @return signed RelayREMMDAcceptanceRejection - Evidence as byte array.
     * @throws ECodexEvidenceBuilderException If an error occurs during the evidence creation.
     */
    byte[] createRelayREMMDAcceptanceRejection(
        boolean isAcceptance, EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidence) throws ECodexEvidenceBuilderException;

    /**
     * Method for building the second evidence from the first one and sign it with an enveloped
     * signature.
     *
     * @param eventReason           List of Reasons for an Error. Allowed eventReasons are
     *                              predefined by large scale project SPOCS.
     * @param evidenceIssuerDetails Details of the connector creating this evidence
     * @param previousEvidence      A SubmissionAcceptanceRejection - Evidence
     * @return signed RelayREMMDFailure - Evidence as byte array.
     * @throws ECodexEvidenceBuilderException If an error occurs during the evidence creation.
     */
    byte[] createRelayREMMDFailure(
        REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidence) throws ECodexEvidenceBuilderException;

    /**
     * Method for building the second evidence from the first one and sign it with an enveloped
     * signature.
     *
     * @param eventReason           List of Reasons for an Error.
     * @param evidenceIssuerDetails Details of the connector creating this evidence
     * @param previousEvidence      A SubmissionAcceptanceRejection - Evidence
     * @return signed RelayREMMDFailure - Evidence as byte array.
     * @throws ECodexEvidenceBuilderException If an error occurs during the evidence creation.
     */
    byte[] createRelayREMMDFailure(
        EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidence) throws ECodexEvidenceBuilderException;

    /**
     * Method for building the second evidence from the first one and sign it with an enveloped
     * signature.
     *
     * @param isDelivery            EventCode ("http:uri.etsi.org/REM/Event#Delivery",
     *                              "http:uri.etsi.org/REM/Event#DeliveryExpiration") of the
     *                              evidence.
     * @param eventReason           List of Reasons for an Error. Ignored when isDelivery == true.
     *                              Allowed eventReasons are predefined by large scale project
     *                              SPOCS.
     * @param evidenceIssuerDetails Details of the connector creating this evidence
     * @param previousEvidence      A SubmissionAcceptanceRejection - Evidence
     * @return signed DeliveryNonDeliveryToRecipient - Evidence as byte array.
     * @throws ECodexEvidenceBuilderException If an error occurs during the evidence creation.
     */
    byte[] createDeliveryNonDeliveryToRecipient(
        boolean isDelivery, REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidence) throws ECodexEvidenceBuilderException;

    /**
     * Method for building the second evidence from the first one and sign it with an enveloped
     * signature.
     *
     * @param isDelivery            EventCode ("http:uri.etsi.org/REM/Event#Delivery",
     *                              "http:uri.etsi.org/REM/Event#DeliveryExpiration") of the
     *                              evidence.
     * @param eventReason           List of Reasons for an Error. Ignored when isAcceptance == true
     * @param evidenceIssuerDetails Details of the connector creating this evidence
     * @param previousEvidence      A SubmissionAcceptanceRejection - Evidence
     * @return signed DeliveryNonDeliveryToRecipient - Evidence as byte array.
     * @throws ECodexEvidenceBuilderException If an error occurs during the evidence creation.
     */
    byte[] createDeliveryNonDeliveryToRecipient(
        boolean isDelivery, EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidence) throws ECodexEvidenceBuilderException;

    /**
     * Method for building the third evidence from the second one and sign it with an enveloped
     * signature.
     *
     * @param isRetrieval           EventCode ("http:uri.etsi.org/REM/Event#Retrieval",
     *                              "http:uri.etsi.org/REM/Event#NonRetrievalExpiration") of the
     *                              evidence.
     * @param eventReason           List of Reasons for an Error. Ignored when isAcceptance == true.
     *                              Allowed eventReasons are predefined by large scale project
     *                              SPOCS.
     * @param evidenceIssuerDetails Details of the connector creating this evidence
     * @param previousEvidence      An already filled REM:Evidence
     * @return signed RetrievalNonRetrievalByRecipient - Evidence as byte array.
     * @throws ECodexEvidenceBuilderException If an error occurs during the evidence creation.
     */
    byte[] createRetrievalNonRetrievalByRecipient(
        boolean isRetrieval, REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidence) throws ECodexEvidenceBuilderException;

    /**
     * Method for building the third evidence from the second one and sign it with an enveloped
     * signature.
     *
     * @param isRetrieval           EventCode ("http:uri.etsi.org/REM/Event#Retrieval",
     *                              "http:uri.etsi.org/REM/Event#NonRetrievalExpiration") of the
     *                              evidence.
     * @param eventReason           List of Reasons for an Error. Ignored when isAcceptance == true
     * @param evidenceIssuerDetails Details of the connector creating this evidence
     * @param previousEvidence      An already filled REM:Evidence
     * @return signed RetrievalNonRetrievalByRecipient - Evidence as byte array.
     * @throws ECodexEvidenceBuilderException If an error occurs during the evidence creation.
     */
    byte[] createRetrievalNonRetrievalByRecipient(
        boolean isRetrieval, EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidence) throws ECodexEvidenceBuilderException;
}
