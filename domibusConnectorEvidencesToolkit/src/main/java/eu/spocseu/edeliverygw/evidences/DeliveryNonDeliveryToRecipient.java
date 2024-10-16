/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import java.io.InputStream;
import java.io.OutputStream;
import org.etsi.uri._02640.v2.EventReasonType;
import org.etsi.uri._02640.v2.ObjectFactory;
import org.etsi.uri._02640.v2.REMEvidenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a DeliveryNonDeliveryToRecipient evidence. It helps to create the
 * underlying REMEvidenceType JAXB object of the xsd structure.
 *
 * @author Lindemann
 */
public class DeliveryNonDeliveryToRecipient extends Evidence {
    private static final Logger LOG = LoggerFactory.getLogger(DeliveryNonDeliveryToRecipient.class);
    public static final String CREATE_DELIVERY_NON_DELIVERY_TO_RECIPIENT_IN_SUCCESS_CASE =
        "Create DeliveryNonDeliveryToRecipient in success case.";
    public static final String CREATE_DELIVERY_NON_DELIVERY_TO_RECIPIENT_IN_FAULT_CASE =
        "Create DeliveryNonDeliveryToRecipient in fault case.";
    private boolean isSuccessful;

    /**
     * This constructor creates this DeliveryNonDeliveryToRecipient evidence with the given JAXB
     * object and the configuration.
     *
     * @param evidenceType The JAXB object.
     */
    public DeliveryNonDeliveryToRecipient(REMEvidenceType evidenceType) {
        super(evidenceType);
    }

    /**
     * This constructor can be used to parse a serialized DeliveryNonDeliveryToRecipient xml stream
     * to create a JAXB evidence object.
     *
     * @param details                      Configuration object to set some properties
     * @param deliveryNonDeliveryInpStream The xml input stream with the evidence xml data.
     * @throws SpocsWrongInputDataException In the case of parsing errors
     */
    public DeliveryNonDeliveryToRecipient(
        EDeliveryDetails details,
        InputStream deliveryNonDeliveryInpStream)
        throws SpocsWrongInputDataException {
        try {
            @SuppressWarnings("unchecked")
            JAXBElement<REMEvidenceType> obj = (JAXBElement<REMEvidenceType>) JaxbContextHolder
                .getSpocsJaxBContext().createUnmarshaller()
                .unmarshal(deliveryNonDeliveryInpStream);
            jaxbObj = obj.getValue();
        } catch (JAXBException ex) {
            throw new SpocsWrongInputDataException(
                "Error reading the DeliveryNonDeliveryToRecipient xml stream.",
                ex
            );
        }
    }

    /**
     * This constructor creates a DeliveryNonDeliveryToRecipient object based on
     * SubmissionAcceptanceRejection evidence.
     *
     * @param details                       Configuration object to set some properties
     * @param submissionAcceptanceRejection The previous SubmissionAcceptanceRejection
     */
    public DeliveryNonDeliveryToRecipient(
        EDeliveryDetails details,
        Evidence submissionAcceptanceRejection) {
        super(details);
        init(details, submissionAcceptanceRejection, true);
    }

    /**
     * This constructor creates a NonDeliveryToRecipient (false) evidence based on
     * SubmissionAcceptanceRejection evidence.
     *
     * @param details                       Configuration object to set some properties
     * @param submissionAcceptanceRejection The previous SubmissionAcceptanceRejection
     */
    // klara
    public DeliveryNonDeliveryToRecipient(
        EDeliveryDetails details,
        Evidence submissionAcceptanceRejection, EventReasonType eventReson) {
        super(details);
        init(details, submissionAcceptanceRejection, false);
        super.setEventReason(eventReson);
    }

    /**
     * This constructor creates a DeliveryNonDeliveryToRecipient object based on previous
     * SubmissionAcceptanceRejection evidence.
     *
     * @param details                       Configuration object to set some properties
     * @param submissionAcceptanceRejection The previous SubmissionAcceptanceRejection
     * @param isDelivery                    If this value is false a fault evidence event will be
     *                                      set.
     */
    public DeliveryNonDeliveryToRecipient(
        EDeliveryDetails details,
        Evidence submissionAcceptanceRejection, boolean isDelivery) {
        super(details);
        init(details, submissionAcceptanceRejection, isDelivery);
    }

    public DeliveryNonDeliveryToRecipient(
        EDeliveryDetails details,
        REMEvidenceType submissionAcceptanceRejection, boolean isDelivery) {
        initEvidenceIssuerDetailsWithEdeliveryDetails(details);
        init(details, submissionAcceptanceRejection, isDelivery);
    }

    /**
     * This method serializes the underlying JAXB object.
     *
     * @param out The output stream that the information will be streamed into.
     */
    public void serialize(OutputStream out) throws JAXBException {
        JaxbContextHolder
            .getSpocsJaxBContext()
            .createMarshaller()
            .marshal(
                new ObjectFactory()
                    .createDeliveryNonDeliveryToRecipient(jaxbObj),
                out
            );
    }

    private void init(
        EDeliveryDetails details, Evidence submissionAcceptanceRejection, boolean isDelivery) {

        evidenceType = Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT;
        if (isDelivery) {
            LOG.debug(CREATE_DELIVERY_NON_DELIVERY_TO_RECIPIENT_IN_SUCCESS_CASE);
            setEventCode(Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT
                             .getSuccessEventCode());
        } else {
            LOG.debug(CREATE_DELIVERY_NON_DELIVERY_TO_RECIPIENT_IN_FAULT_CASE);
            setEventCode(Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT
                             .getFaultEventCode());
        }
        initWithPrevious(submissionAcceptanceRejection.getXSDObject());
        isSuccessful = isDelivery;
    }

    private void init(
        EDeliveryDetails details,
        REMEvidenceType submissionAcceptanceRejection, boolean isDelivery) {

        evidenceType = Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT;
        if (isDelivery) {
            LOG.debug(CREATE_DELIVERY_NON_DELIVERY_TO_RECIPIENT_IN_SUCCESS_CASE);
            setEventCode(Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT
                             .getSuccessEventCode());
        } else {
            LOG.debug(CREATE_DELIVERY_NON_DELIVERY_TO_RECIPIENT_IN_FAULT_CASE);
            setEventCode(Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT
                             .getFaultEventCode());
        }
        initWithPrevious(submissionAcceptanceRejection);
        isSuccessful = isDelivery;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
