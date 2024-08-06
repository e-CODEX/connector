/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

/* ---------------------------------------------------------------------------
             COMPETITIVENESS AND INNOVATION FRAMEWORK PROGRAMME
                   ICT Policy Support Programme (ICT PSP)
           Preparing the implementation of the Services Directive
                   ICT PSP call identifier: ICT PSP-2008-2
             ICT PSP main Theme identifier: CIP-ICT-PSP.2008.1.1
                           Project acronym: SPOCS
   Project full title: Simple Procedures Online for Cross-border Services
                         Grant agreement no.: 238935
                               www.eu-spocs.eu
------------------------------------------------------------------------------
    WP3 Interoperable delivery, eSafe, secure and interoperable exchanges
                       and acknowledgement of receipt
------------------------------------------------------------------------------
        Open module implementing the eSafe document exchange protocol
------------------------------------------------------------------------------

$URL: svn:https://svnext.bos-bremen.de/SPOCS/AllWpImplementation/EDelivery-Gateway
$Date: 2010-05-13 18:55:57 +0200 (Do, 14. Okt 2010) $
$Revision: 86 $

See SPOCS_WP3_LICENSE_URL for license information
--------------------------------------------------------------------------- */

package eu.spocseu.edeliverygw.evidences;

import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.edeliverygw.JaxbContextHolder;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;
import java.io.OutputStream;
import java.util.UUID;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.etsi.uri._02640.soapbinding.v1_.REMDispatchType;
import org.etsi.uri._02640.v2.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2.EntityDetailsListType;
import org.etsi.uri._02640.v2.EntityDetailsType;
import org.etsi.uri._02640.v2.MessageDetailsType;
import org.etsi.uri._02640.v2.ObjectFactory;
import org.etsi.uri._02640.v2.REMEvidenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a SubmissionAcceptanceRejection evidence. It helps to create the underlying
 * REMEvidenceType JAXB object of the xsd structure.
 *
 * @author Lindemann
 */
public class SubmissionAcceptanceRejection extends Evidence {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(SubmissionAcceptanceRejection.class);
    public static final String CREATE_SUBMISSION_ACCEPTANCE_REJECTION_IN_SUCCESS_CASE =
        "Create SubmissionAcceptanceRejection in success case.";
    public static final String CREATE_SUBMISSION_ACCEPTANCE_REJECTION_IN_FAULT_CASE =
        "Create SubmissionAcceptanceRejection in fault case.";

    protected SubmissionAcceptanceRejection(EDeliveryDetails details) {
        super(details);
    }

    protected SubmissionAcceptanceRejection(
        EDeliveryDetails details,
        boolean isAcceptance) {
        super(details);
        if (isAcceptance) {
            LOGGER.debug(CREATE_SUBMISSION_ACCEPTANCE_REJECTION_IN_SUCCESS_CASE);
            setEventCode(Evidences.SUBMISSION_ACCEPTANCE_REJECTION
                             .getSuccessEventCode());
        } else {
            LOGGER.debug(CREATE_SUBMISSION_ACCEPTANCE_REJECTION_IN_FAULT_CASE);
            setEventCode(Evidences.SUBMISSION_ACCEPTANCE_REJECTION
                             .getFaultEventCode());
        }
    }

    /**
     * This constructor creates a SubmissionAcceptanceRejection object based on a given
     * DispatchMessage. The purpose is to create the SubmissionAcceptanceRejection which will be
     * attached to the Dispatch Message which will be sent out.
     *
     * @param details      Configuration object to set some properties
     * @param dispatch     DispatchMessage as input information for this object. This is a Dispatch
     *                     which should be sent out!
     * @param isAcceptance If true is given a success event will be created.
     */
    public SubmissionAcceptanceRejection(
        EDeliveryDetails details,
        REMDispatchType dispatch, boolean isAcceptance) {
        super(details);
        initWithDispatch(details, dispatch, isAcceptance);
    }

    /**
     * Creates a SubmissionAcceptanceRejection evidence object with the given JAXB object.
     *
     * @param evidenceType The JAXB object
     */
    public SubmissionAcceptanceRejection(REMEvidenceType evidenceType) {
        super(evidenceType);
    }

    private void initWithDispatch(
        EDeliveryDetails details,
        REMDispatchType dispatch, boolean isAcceptance) {

        evidenceType = Evidences.SUBMISSION_ACCEPTANCE_REJECTION;
        if (isAcceptance) {
            LOGGER.debug(CREATE_SUBMISSION_ACCEPTANCE_REJECTION_IN_SUCCESS_CASE);
            setEventCode(Evidences.SUBMISSION_ACCEPTANCE_REJECTION
                             .getSuccessEventCode());
        } else {
            LOGGER.debug(CREATE_SUBMISSION_ACCEPTANCE_REJECTION_IN_FAULT_CASE);
            LOGGER.debug(
                "FaultCode: {}", Evidences.SUBMISSION_ACCEPTANCE_REJECTION.getFaultEventCode()
            );
            setEventCode(Evidences.SUBMISSION_ACCEPTANCE_REJECTION
                             .getFaultEventCode()
            );
        }
        EntityDetailsType from = dispatch.getMsgMetaData().getOriginators()
                                         .getFrom();
        String senderEAddress = ((AttributedElectronicAddressType) from
            .getAttributedElectronicAddressOrElectronicAddress().getFirst()
        )
            .getValue();

        String senderName = ((AttributedElectronicAddressType) from
            .getAttributedElectronicAddressOrElectronicAddress().getFirst()
        )
            .getDisplayName();

        String[] senderPostalName = null;
        if (dispatch.getMsgMetaData().getOriginators().getFrom()
                    .getNamesPostalAddresses() != null) {
            senderPostalName = new String[dispatch
                .getMsgMetaData()
                .getOriginators().getFrom()
                .getNamesPostalAddresses()
                .getNamePostalAddress().getFirst().getEntityName()
                .getName()
                .size()];

            dispatch.getMsgMetaData().getOriginators().getFrom()
                    .getNamesPostalAddresses().getNamePostalAddress().getFirst()
                    .getEntityName().getName().toArray(senderPostalName);
        }

        jaxbObj.setSenderDetails(createEntityDetailsType(senderEAddress,
                                                         senderName, senderPostalName
        ));
        var messageDetailsType = new MessageDetailsType();
        messageDetailsType.setMessageIdentifierByREMMD(
            dispatch.getMsgMetaData().getMsgIdentification().getMessageID());
        // NormalizedMsg norMsg = dispatch.getNormalizedMsg();
        // if (norMsg != null && norMsg.getInformational() != null)
        // messageDetailsType.setMessageSubject(norMsg.getInformational()
        //.getSubject());
        jaxbObj.setSenderMessageDetails(messageDetailsType);

        EntityDetailsType recipient = dispatch.getMsgMetaData().getDestinations().getRecipient();

        var recipientAddress = SpocsFragments.getAttributedElectronicAddress(recipient).getValue();
        var recipientName = SpocsFragments.getAttributedElectronicAddress(recipient)
                                          .getDisplayName();
        var recipientPostalNames = new String[0];

        if (dispatch.getMsgMetaData().getDestinations().getRecipient()
                    .getNamesPostalAddresses() != null
            && dispatch.getMsgMetaData().getDestinations().getRecipient()
                       .getNamesPostalAddresses().getNamePostalAddress()
                       .getFirst() != null) {
            recipientPostalNames = dispatch
                .getMsgMetaData().getDestinations()
                .getRecipient().getNamesPostalAddresses()
                .getNamePostalAddress().getFirst().getEntityName().getName()
                .toArray(recipientPostalNames);
        }
        var detailList = new EntityDetailsListType();
        detailList.getEntityDetails().add(
            createEntityDetailsType(recipientAddress, recipientName,
                                    recipientPostalNames
            ));
        jaxbObj.setRecipientsDetails(detailList);
        jaxbObj.setSubmissionTime(dispatch.getMsgMetaData()
                                          .getDeliveryConstraints().getInitialSend());
        if (dispatch.getMsgMetaData().getOriginators() != null
            && dispatch.getMsgMetaData().getOriginators().getReplyTo() != null
            && dispatch.getMsgMetaData().getOriginators().getReplyTo()
                       .getAttributedElectronicAddressOrElectronicAddress() != null) {
            AttributedElectronicAddressType electronicAddressType =
                SpocsFragments.getFirstElectronicAddressWithURI(
                    dispatch.getMsgMetaData().getOriginators().getReplyTo());
            jaxbObj.setReplyToAddress(electronicAddressType);
        } else if (dispatch.getMsgMetaData().getOriginators() != null) {
            AttributedElectronicAddressType electronicAddressType = SpocsFragments
                .getAttributedElectronicAddress(
                    dispatch.getMsgMetaData().getOriginators().getFrom()
                );
            jaxbObj.setReplyToAddress(electronicAddressType);
        }
        jaxbObj.setId(UUID.randomUUID().toString());
    }

    /**
     * This method serializes the underlying JAXB object.
     *
     * @param out The output stream that the information will be streamed into.
     */
    public void serialize(OutputStream out) throws JAXBException {
        JAXBElement<REMEvidenceType> output =
            new ObjectFactory().createSubmissionAcceptanceRejection(jaxbObj);

        var m = JaxbContextHolder
            .getSpocsJaxBContext()
            .createMarshaller();

        m.marshal(output, out);
    }
}
