package eu.ecodex.evidences;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.GregorianCalendar;

import jakarta.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import eu.domibus.connector.common.annotations.BusinessDomainScoped;
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

import eu.ecodex.evidences.exception.ECodexEvidenceBuilderException;
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
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@BusinessDomainScoped
public class ECodexEvidenceBuilder implements EvidenceBuilder {
    private static Logger LOG = Logger.getLogger(ECodexEvidenceBuilder.class);

    private static EvidenceUtils signer = null;

    public ECodexEvidenceBuilder(Resource javaKeyStorePath, String javaKeyStoreType, String javaKeyStorePassword, String alias, String keyPassword) {
        // signer = new EvidenceUtilsImpl(javaKeyStorePath,
        // javaKeyStorePassword, alias, keyPassword);
        signer = new EvidenceUtilsXades(javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, alias, keyPassword);
    }

    @Override
    public byte[] createSubmissionAcceptanceRejection(boolean isAcceptance, REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails, ECodexMessageDetails messageDetails)
            throws ECodexEvidenceBuilderException {

        EventReasonType reason = null;

        if (eventReason != null) {
            reason = new EventReasonType();
            reason.setCode(eventReason.getEventCode());
            reason.setDetails(eventReason.getEventDetails());
        }

        return createSubmissionAcceptanceRejection(isAcceptance, reason, evidenceIssuerDetails, messageDetails);
    }

    @Override
    public byte[] createSubmissionAcceptanceRejection(boolean isAcceptance, EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails, ECodexMessageDetails messageDetails)
            throws ECodexEvidenceBuilderException {
        Date start = new Date();

        // This is the message and all related information
        REMDispatchType dispatch = new REMDispatchType();

        MsgMetaData msgMetaData = new MsgMetaData();
        EntityDetailsType recipient = new EntityDetailsType();
        EntityDetailsType sender = new EntityDetailsType();
        try {
            recipient.getAttributedElectronicAddressOrElectronicAddress().add(SpocsFragments.createElectoricAddress(messageDetails.getSenderAddress(), "displayName"));
            sender.getAttributedElectronicAddressOrElectronicAddress().add(SpocsFragments.createElectoricAddress(messageDetails.getRecipientAddress(), "displayName"));
        } catch (MalformedURLException e) {
            LOG.warn(e);
        }

        Destinations destinations = new Destinations();
        destinations.setRecipient(sender);

        Originators originators = new Originators();
        originators.setFrom(recipient);
        originators.setReplyTo(recipient);
        originators.setSender(recipient);

        MsgIdentification msgIdentification = new MsgIdentification();
        msgIdentification.setMessageID(messageDetails.getEbmsMessageId());

        msgMetaData.setDestinations(destinations);
        msgMetaData.setOriginators(originators);
        msgMetaData.setMsgIdentification(msgIdentification);

        GregorianCalendar cal = new GregorianCalendar();
        XMLGregorianCalendar initialSend = null;
        try {
            initialSend = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e1) {
            e1.printStackTrace();
        }
        DeliveryConstraints deliveryConstraints = new DeliveryConstraints();
        deliveryConstraints.setInitialSend(initialSend);

        msgMetaData.setDeliveryConstraints(deliveryConstraints);

        dispatch.setMsgMetaData(msgMetaData);

        SubmissionAcceptanceRejection evidence = new SubmissionAcceptanceRejection(evidenceIssuerDetails, dispatch, isAcceptance);

        if (eventReason != null)
            evidence.setEventReason(eventReason);

        evidence.setUAMessageId(messageDetails.getNationalMessageId());
        evidence.setHashInformation(messageDetails.getHashValue(), messageDetails.getHashAlgorithm());

        byte[] signedByteArray = signEvidence(evidence, false);

        LOG.info("Creation of SubmissionAcceptanceRejection Evidence finished in " + (System.currentTimeMillis() - start.getTime()) + " ms.");

        return signedByteArray;
    }

    @Override
    public byte[] createRelayREMMDAcceptanceRejection(boolean isAcceptance, REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails, byte[] previousEvidenceInByte)
            throws ECodexEvidenceBuilderException {

        EventReasonType reason = null;

        if (eventReason != null) {
            reason = new EventReasonType();
            reason.setCode(eventReason.getEventCode());
            reason.setDetails(eventReason.getEventDetails());
        }

        return createRelayREMMDAcceptanceRejection(isAcceptance, reason, evidenceIssuerDetails, previousEvidenceInByte);
    }

    @Override
    public byte[] createRelayREMMDAcceptanceRejection(boolean isAcceptance, EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails, byte[] previousEvidenceInByte)
            throws ECodexEvidenceBuilderException {

        REMEvidenceType previousEvidence = signer.convertIntoEvidenceType(previousEvidenceInByte);

        RelayREMMDAcceptanceRejection evidence = new RelayREMMDAcceptanceRejection(evidenceIssuerDetails, previousEvidence, isAcceptance);

        if (eventReason != null)
            evidence.setEventReason(eventReason);

        byte[] signedByteArray = signEvidence(evidence, true);

        return signedByteArray;
    }

    @Override
    public byte[] createRelayREMMDFailure(REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails, byte[] previousEvidenceInByte)
            throws ECodexEvidenceBuilderException {

        EventReasonType reason = null;

        if (eventReason != null) {
            reason = new EventReasonType();
            reason.setCode(eventReason.getEventCode());
            reason.setDetails(eventReason.getEventDetails());
        }

        return createRelayREMMDFailure(reason, evidenceIssuerDetails, previousEvidenceInByte);
    }

    @Override
    public byte[] createRelayREMMDFailure(EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails, byte[] previousEvidenceInByte)
            throws ECodexEvidenceBuilderException {

        REMEvidenceType previousEvidence = signer.convertIntoEvidenceType(previousEvidenceInByte);

        RelayREMMDFailure evidence = new RelayREMMDFailure(evidenceIssuerDetails, previousEvidence);

        if (eventReason != null)
            evidence.setEventReason(eventReason);

        byte[] signedByteArray = signEvidence(evidence, true);

        return signedByteArray;
    }

    @Override
    public byte[] createDeliveryNonDeliveryToRecipient(boolean isDelivery, REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails, byte[] previousEvidenceInByte)
            throws ECodexEvidenceBuilderException {
        EventReasonType reason = null;

        if (eventReason != null) {
            reason = new EventReasonType();
            reason.setCode(eventReason.getEventCode());
            reason.setDetails(eventReason.getEventDetails());
        }

        return createDeliveryNonDeliveryToRecipient(isDelivery, reason, evidenceIssuerDetails, previousEvidenceInByte);
    }

    @Override
    public byte[] createDeliveryNonDeliveryToRecipient(boolean isDelivery, EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails, byte[] previousEvidenceInByte)
            throws ECodexEvidenceBuilderException {

        REMEvidenceType previousEvidence = signer.convertIntoEvidenceType(previousEvidenceInByte);

        DeliveryNonDeliveryToRecipient evidence = new DeliveryNonDeliveryToRecipient(evidenceIssuerDetails, previousEvidence, isDelivery);

        if (eventReason != null)
            evidence.setEventReason(eventReason);

        byte[] signedByteArray = signEvidence(evidence, true);

        return signedByteArray;
    }

    @Override
    public byte[] createRetrievalNonRetrievalByRecipient(boolean isRetrieval, REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails, byte[] previousEvidenceInByte)
            throws ECodexEvidenceBuilderException {

        EventReasonType reason = null;

        if (eventReason != null) {
            reason = new EventReasonType();
            reason.setCode(eventReason.getEventCode());
            reason.setDetails(eventReason.getEventDetails());
        }

        return createRetrievalNonRetrievalByRecipient(isRetrieval, reason, evidenceIssuerDetails, previousEvidenceInByte);

    }

    @Override
    public byte[] createRetrievalNonRetrievalByRecipient(boolean isRetrieval, EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails, byte[] previousEvidenceInByte)
            throws ECodexEvidenceBuilderException {

        REMEvidenceType previousEvidence = signer.convertIntoEvidenceType(previousEvidenceInByte);

        RetrievalNonRetrievalByRecipient evidence = new RetrievalNonRetrievalByRecipient(evidenceIssuerDetails, previousEvidence, isRetrieval);

        if (eventReason != null)
            evidence.setEventReason(eventReason);

        byte[] signedByteArray = signEvidence(evidence, true);

        return signedByteArray;
    }

    private byte[] signEvidence(Evidence evidenceToBeSigned, boolean removeOldSignature) {

        if (removeOldSignature) {
            // delete old signature field
            evidenceToBeSigned.getXSDObject().setSignature(null);
            LOG.debug("Old Signature removed");
        }

        ByteArrayOutputStream fo = new ByteArrayOutputStream();

        try {

            evidenceToBeSigned.serialize(fo);
        } catch (JAXBException e) {
            LOG.error("Cannot serialize evidence", e);
        }

        byte[] bytes = fo.toByteArray();

        byte[] signedByteArray = signer.signByteArray(bytes);

        return signedByteArray;
    }

}
