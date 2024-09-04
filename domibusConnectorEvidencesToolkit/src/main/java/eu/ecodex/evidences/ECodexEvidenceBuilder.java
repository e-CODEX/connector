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

import eu.ecodex.connector.common.annotations.BusinessDomainScoped;
import eu.ecodex.evidences.types.ECodexMessageDetails;
import eu.ecodex.signature.EvidenceUtils;
import eu.ecodex.signature.EvidenceUtilsXades;
import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.evidences.Evidence;
import eu.spocseu.edeliverygw.evidences.RelayREMMDAcceptanceRejection;
import eu.spocseu.edeliverygw.evidences.RelayREMMDFailure;
import eu.spocseu.edeliverygw.evidences.RetrievalNonRetrievalByRecipient;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;
import jakarta.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.log4j.Logger;
import org.etsi.uri._02640.soapbinding.v1_.DeliveryConstraints;
import org.etsi.uri._02640.soapbinding.v1_.Destinations;
import org.etsi.uri._02640.soapbinding.v1_.MsgIdentification;
import org.etsi.uri._02640.soapbinding.v1_.MsgMetaData;
import org.etsi.uri._02640.soapbinding.v1_.Originators;
import org.etsi.uri._02640.soapbinding.v1_.REMDispatchType;
import org.etsi.uri._02640.v2.EntityDetailsType;
import org.etsi.uri._02640.v2.EventReasonType;
import org.etsi.uri._02640.v2.REMEvidenceType;
import org.springframework.core.io.Resource;

/**
 * The ECodexEvidenceBuilder class is responsible for creating various types of evidence related to
 * electronic communication in the eCodex system. It implements the EvidenceBuilder interface.
 */
@SuppressWarnings("squid:S125")
@BusinessDomainScoped
public class ECodexEvidenceBuilder implements EvidenceBuilder {
    private static final Logger LOGGER = Logger.getLogger(ECodexEvidenceBuilder.class);
    private final EvidenceUtils signer;

    /**
     * Initializes an instance of ECodexEvidenceBuilder with the provided parameters.
     *
     * @param javaKeyStorePath     The path to the Java keystore file.
     * @param javaKeyStoreType     The type of the Java keystore.
     * @param javaKeyStorePassword The password of the Java keystore.
     * @param alias                The alias for the key in the keystore.
     * @param keyPassword          The password of the key in the keystore.
     */
    public ECodexEvidenceBuilder(
        Resource javaKeyStorePath, String javaKeyStoreType, String javaKeyStorePassword,
        String alias, String keyPassword) {
        // signer = new EvidenceUtilsImpl(javaKeyStorePath,
        // javaKeyStorePassword, alias, keyPassword);
        signer = new EvidenceUtilsXades(
            javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword
        );
    }

    @Override
    public byte[] createSubmissionAcceptanceRejection(
        boolean isAcceptance, REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails,
        ECodexMessageDetails messageDetails) {

        EventReasonType reason = null;

        if (eventReason != null) {
            reason = new EventReasonType();
            reason.setCode(eventReason.getEventCode());
            reason.setDetails(eventReason.getEventDetails());
        }

        return createSubmissionAcceptanceRejection(
            isAcceptance, reason, evidenceIssuerDetails, messageDetails);
    }

    @Override
    public byte[] createSubmissionAcceptanceRejection(
        boolean isAcceptance, EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails,
        ECodexMessageDetails messageDetails) {

        // This is the message and all related information

        var recipient = new EntityDetailsType();
        var sender = new EntityDetailsType();
        try {
            recipient.getAttributedElectronicAddressOrElectronicAddress().add(
                SpocsFragments.createElectronicAddress(
                    messageDetails.getSenderAddress(),
                    "displayName"
                ));
            sender.getAttributedElectronicAddressOrElectronicAddress().add(
                SpocsFragments.createElectronicAddress(
                    messageDetails.getRecipientAddress(),
                    "displayName"
                ));
        } catch (MalformedURLException e) {
            LOGGER.warn(e);
        }

        var destinations = new Destinations();
        destinations.setRecipient(sender);

        var originators = new Originators();
        originators.setFrom(recipient);
        originators.setReplyTo(recipient);
        originators.setSender(recipient);

        var msgIdentification = new MsgIdentification();
        msgIdentification.setMessageID(messageDetails.getEbmsMessageId());

        var msgMetaData = new MsgMetaData();
        msgMetaData.setDestinations(destinations);
        msgMetaData.setOriginators(originators);
        msgMetaData.setMsgIdentification(msgIdentification);

        var cal = new GregorianCalendar();
        XMLGregorianCalendar initialSend = null;
        try {
            initialSend = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e1) {
            e1.printStackTrace();
        }
        var deliveryConstraints = new DeliveryConstraints();
        deliveryConstraints.setInitialSend(initialSend);

        msgMetaData.setDeliveryConstraints(deliveryConstraints);

        var dispatch = new REMDispatchType();
        dispatch.setMsgMetaData(msgMetaData);

        var evidence = new SubmissionAcceptanceRejection(
            evidenceIssuerDetails, dispatch, isAcceptance
        );

        if (eventReason != null) {
            evidence.setEventReason(eventReason);
        }

        evidence.setUAMessageId(messageDetails.getNationalMessageId());
        evidence.setHashInformation(
            messageDetails.getHashValue(), messageDetails.getHashAlgorithm());

        byte[] signedByteArray = signEvidence(evidence, false);

        var start = new Date();

        LOGGER.info("Creation of SubmissionAcceptanceRejection Evidence finished in " + (
            System.currentTimeMillis() - start.getTime()
        ) + " ms.");

        return signedByteArray;
    }

    @Override
    public byte[] createRelayREMMDAcceptanceRejection(
        boolean isAcceptance, REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidenceInByte) {

        EventReasonType reason = null;

        if (eventReason != null) {
            reason = new EventReasonType();
            reason.setCode(eventReason.getEventCode());
            reason.setDetails(eventReason.getEventDetails());
        }

        return createRelayREMMDAcceptanceRejection(
            isAcceptance, reason, evidenceIssuerDetails, previousEvidenceInByte);
    }

    @Override
    public byte[] createRelayREMMDAcceptanceRejection(
        boolean isAcceptance, EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidenceInByte) {

        REMEvidenceType previousEvidence = signer.convertIntoEvidenceType(previousEvidenceInByte);

        var evidence = new RelayREMMDAcceptanceRejection(
            evidenceIssuerDetails, previousEvidence, isAcceptance
        );

        if (eventReason != null) {
            evidence.setEventReason(eventReason);
        }

        return signEvidence(evidence, true);
    }

    @Override
    public byte[] createRelayREMMDFailure(
        REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidenceInByte) {

        EventReasonType reason = null;

        if (eventReason != null) {
            reason = new EventReasonType();
            reason.setCode(eventReason.getEventCode());
            reason.setDetails(eventReason.getEventDetails());
        }

        return createRelayREMMDFailure(reason, evidenceIssuerDetails, previousEvidenceInByte);
    }

    @Override
    public byte[] createRelayREMMDFailure(
        EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidenceInByte) {

        REMEvidenceType previousEvidence = signer.convertIntoEvidenceType(previousEvidenceInByte);

        var evidence = new RelayREMMDFailure(evidenceIssuerDetails, previousEvidence);

        if (eventReason != null) {
            evidence.setEventReason(eventReason);
        }

        return signEvidence(evidence, true);
    }

    @Override
    public byte[] createDeliveryNonDeliveryToRecipient(
        boolean isDelivery, REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidenceInByte) {
        EventReasonType reason = null;

        if (eventReason != null) {
            reason = new EventReasonType();
            reason.setCode(eventReason.getEventCode());
            reason.setDetails(eventReason.getEventDetails());
        }

        return createDeliveryNonDeliveryToRecipient(
            isDelivery, reason, evidenceIssuerDetails, previousEvidenceInByte);
    }

    @Override
    public byte[] createDeliveryNonDeliveryToRecipient(
        boolean isDelivery, EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidenceInByte) {

        REMEvidenceType previousEvidence = signer.convertIntoEvidenceType(previousEvidenceInByte);

        var evidence = new DeliveryNonDeliveryToRecipient(
            evidenceIssuerDetails, previousEvidence, isDelivery
        );

        if (eventReason != null) {
            evidence.setEventReason(eventReason);
        }

        return signEvidence(evidence, true);
    }

    @Override
    public byte[] createRetrievalNonRetrievalByRecipient(
        boolean isRetrieval, REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidenceInByte) {

        EventReasonType reason = null;

        if (eventReason != null) {
            reason = new EventReasonType();
            reason.setCode(eventReason.getEventCode());
            reason.setDetails(eventReason.getEventDetails());
        }

        return createRetrievalNonRetrievalByRecipient(
            isRetrieval, reason, evidenceIssuerDetails, previousEvidenceInByte);
    }

    @Override
    public byte[] createRetrievalNonRetrievalByRecipient(
        boolean isRetrieval, EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails,
        byte[] previousEvidenceInByte) {

        REMEvidenceType previousEvidence = signer.convertIntoEvidenceType(previousEvidenceInByte);

        var evidence = new RetrievalNonRetrievalByRecipient(
            evidenceIssuerDetails, previousEvidence, isRetrieval
        );

        if (eventReason != null) {
            evidence.setEventReason(eventReason);
        }

        return signEvidence(evidence, true);
    }

    private byte[] signEvidence(Evidence evidenceToBeSigned, boolean removeOldSignature) {

        if (removeOldSignature) {
            // delete old signature field
            evidenceToBeSigned.getXSDObject().setSignature(null);
            LOGGER.debug("Old Signature removed");
        }

        var fo = new ByteArrayOutputStream();

        try {

            evidenceToBeSigned.serialize(fo);
        } catch (JAXBException e) {
            LOGGER.error("Cannot serialize evidence", e);
        }

        byte[] bytes = fo.toByteArray();

        return signer.signByteArray(bytes);
    }
}
