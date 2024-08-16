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
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.etsi.uri._02640.v2.ObjectFactory;
import org.etsi.uri._02640.v2.REMEvidenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a RelayREMMDAcceptanceRejection evidence. It helps to create the underlying
 * REMEvidenceType JAXB object of the xsd structure.
 *
 * @author Lindemann
 */
public class RelayREMMDAcceptanceRejection extends Evidence {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(RelayREMMDAcceptanceRejection.class);
    public static final String CREATE_RELAY_REMMDACCEPTANCE_REJECTION_IN_FAULT_CASE =
        "Create RelayREMMDAcceptanceRejection in fault case.";
    public static final String CREATE_RELAY_REMMDACCEPTANCE_REJECTION_IN_SUCCESS_CASE =
        "Create RelayREMMDAcceptanceRejection in success case.";
    private boolean isSuccessful;

    /**
     * This constructor creates this RelayREMMDAcceptanceRejection evidence with the given JAXB
     * object and the configuration.
     *
     * @param evidenceType The JAXB object.
     */
    public RelayREMMDAcceptanceRejection(REMEvidenceType evidenceType) {
        super(evidenceType);
    }

    /**
     * This constructor can be used to parse a serialized RelayREMMDAcceptanceRejection xml stream
     * to create a JAXB evidence object.
     *
     * @param details                                Configuration object to set some properties
     * @param relayREMMDAcceptanceRejectionInpStream The xml input stream with the evidence xml
     *                                               data.
     * @throws SpocsWrongInputDataException In the case of parsing errors
     */
    public RelayREMMDAcceptanceRejection(
        EDeliveryDetails details,
        InputStream relayREMMDAcceptanceRejectionInpStream)
        throws SpocsWrongInputDataException {
        try {
            @SuppressWarnings("unchecked")
            JAXBElement<REMEvidenceType> obj = (JAXBElement<REMEvidenceType>) JaxbContextHolder
                .getSpocsJaxBContext().createUnmarshaller()
                .unmarshal(relayREMMDAcceptanceRejectionInpStream);
            jaxbObj = obj.getValue();
        } catch (JAXBException ex) {
            throw new SpocsWrongInputDataException(
                "Error reading the RelayREMMDAcceptanceRejection xml stream.",
                ex
            );
        }
    }

    /**
     * This constructor creates a RelayREMMDAcceptanceRejection object on base of a previous
     * SubmissionAcceptanceRejection evidence. A success event will be set by this constructor.
     *
     * @param details                       Configuration object to set some properties
     * @param submissionAcceptanceRejection The previous SubmissionAcceptanceRejection
     */
    public RelayREMMDAcceptanceRejection(
        EDeliveryDetails details,
        Evidence submissionAcceptanceRejection) {
        super(details);
        init(submissionAcceptanceRejection, true);
    }

    /**
     * This constructor creates a DeliveryNonDeliveryToRecipient object based on previous
     * SubmissionAcceptanceRejection evidence.
     *
     * @param details                       Configuration object to set some properties
     * @param submissionAcceptanceRejection The previous SubmissionAcceptanceRejection
     * @param isAcceptance                  If this value is false a fault evidence event will be
     *                                      set.
     */
    public RelayREMMDAcceptanceRejection(
        EDeliveryDetails details,
        Evidence submissionAcceptanceRejection, boolean isAcceptance) {
        super(details);
        init(submissionAcceptanceRejection, isAcceptance);
    }

    public RelayREMMDAcceptanceRejection(
        EDeliveryDetails details,
        REMEvidenceType submissionAcceptanceRejection, boolean isDelivery) {
        initEvidenceIssuerDetailsWithEdeliveryDetails(details);
        init(details, submissionAcceptanceRejection, isDelivery);
    }

    /**
     * This constructor creates a RelayREMMDAcceptanceRejection evidence with the given
     * singleEvidence and details. For internal use only.
     *
     * @param singleEvidence The single evidence that the RelayREMMDAcceptanceRejection is based
     *                       on.
     * @param details        The delivery details for the evidence.
     */
    public RelayREMMDAcceptanceRejection(
        REMEvidenceType singleEvidence,
        EDeliveryDetails details) {
        super(details);
        evidenceType = Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION;
        LOGGER.debug(CREATE_RELAY_REMMDACCEPTANCE_REJECTION_IN_FAULT_CASE);
        setEventCode(Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION
                         .getFaultEventCode());
        // REMEvidenceType singleEvidence = message.getEvidence(
        // Evidences.SUBMISSION_ACCEPTANCE_REJECTION, message.getXSDObject().getREMMDEvidenceList()
        // );
        initWithPrevious(singleEvidence);
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
                    .createRelayREMMDAcceptanceRejection(jaxbObj),
                out
            );
    }

    private void init(
        Evidence submissionAcceptanceRejection,
        boolean isAcceptance) {

        evidenceType = Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION;
        if (isAcceptance) {
            LOGGER.debug(CREATE_RELAY_REMMDACCEPTANCE_REJECTION_IN_SUCCESS_CASE);
            setEventCode(Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION
                             .getSuccessEventCode());
        } else {
            LOGGER.debug(CREATE_RELAY_REMMDACCEPTANCE_REJECTION_IN_FAULT_CASE);
            setEventCode(Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION
                             .getFaultEventCode());
        }
        initWithPrevious(submissionAcceptanceRejection.getXSDObject());
    }

    private void init(
        EDeliveryDetails details,
        REMEvidenceType submissionAcceptanceRejection, boolean isDelivery) {

        evidenceType = Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION;
        if (isDelivery) {
            LOGGER.debug(CREATE_RELAY_REMMDACCEPTANCE_REJECTION_IN_SUCCESS_CASE);
            setEventCode(Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION
                             .getSuccessEventCode());
        } else {
            LOGGER.debug(CREATE_RELAY_REMMDACCEPTANCE_REJECTION_IN_FAULT_CASE);
            setEventCode(Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION
                             .getFaultEventCode());
        }
        initWithPrevious(submissionAcceptanceRejection);
        isSuccessful = isDelivery;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
