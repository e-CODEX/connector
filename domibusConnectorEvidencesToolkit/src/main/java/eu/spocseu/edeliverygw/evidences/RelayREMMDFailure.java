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
 * This class represents a RelayREMMDFailure evidence. It helps to create the underlying
 * REMEvidenceType JAXB object of the XSD structure.
 *
 * @author Lindemann
 */
public class RelayREMMDFailure extends Evidence {
    private static final Logger LOG = LoggerFactory.getLogger(RelayREMMDAcceptanceRejection.class);

    /**
     * This constructor creates this RelayREMMDFailure evidence with the given JAXB object and the
     * configuration.
     *
     * @param evidenceType The JAXB object.
     */
    public RelayREMMDFailure(REMEvidenceType evidenceType) {
        super(evidenceType);
    }

    /**
     * This method creates a RelayREMMDFailure object with the given delivery details and input
     * stream of the xml data. It unmarshals the xml data using JAXB and sets the unmarshalled
     * object value to the jaxbObj field.
     *
     * @param details                    The delivery details.
     * @param relayREMMDFailureInpStream The input stream of the xml data.
     * @throws SpocsWrongInputDataException If there is an error reading the xml stream.
     */
    public RelayREMMDFailure(
        EDeliveryDetails details,
        InputStream relayREMMDFailureInpStream)
        throws SpocsWrongInputDataException {
        try {
            @SuppressWarnings("unchecked")
            JAXBElement<REMEvidenceType> obj = (JAXBElement<REMEvidenceType>) JaxbContextHolder
                .getSpocsJaxBContext().createUnmarshaller()
                .unmarshal(relayREMMDFailureInpStream);
            jaxbObj = obj.getValue();
        } catch (JAXBException ex) {
            throw new SpocsWrongInputDataException(
                "Error reading the RelayREMMDFailure xml stream.",
                ex
            );
        }
    }

    /**
     * This constructor creates a RelayREMMDFailure object on base of a previous
     * SubmissionAcceptanceRejection evidence. A success event will be set by this constructor.
     *
     * @param details                       Configuration object to set some properties
     * @param submissionAcceptanceRejection The previous SubmissionAcceptanceRejection
     */
    public RelayREMMDFailure(
        EDeliveryDetails details,
        Evidence submissionAcceptanceRejection) {
        super(details);
        init(submissionAcceptanceRejection);
    }

    public RelayREMMDFailure(
        EDeliveryDetails details,
        REMEvidenceType submissionAcceptanceRejection) {
        initEvidenceIssuerDetailsWithEdeliveryDetails(details);
        init(details, submissionAcceptanceRejection);
    }

    /**
     * Constructs a RelayREMMDFailure evidence object with the given single evidence and delivery
     * details.
     *
     * @param singleEvidence The single evidence of REMEvidenceType.
     * @param details        The delivery details.
     */
    public RelayREMMDFailure(
        REMEvidenceType singleEvidence,
        EDeliveryDetails details) {
        super(details);
        evidenceType = Evidences.RELAY_REM_MD_FAILURE;
        LOG.debug("Create RelayREMMDFailure.");
        setEventCode(Evidences.RELAY_REM_MD_FAILURE.getFaultEventCode());
        initWithPrevious(singleEvidence);
    }

    private void init(Evidence submissionAcceptanceRejection) {

        evidenceType = Evidences.RELAY_REM_MD_FAILURE;
        LOG.debug("Create RelayREMMDFailure in fault case.");
        setEventCode(Evidences.RELAY_REM_MD_FAILURE.getFaultEventCode());

        initWithPrevious(submissionAcceptanceRejection.getXSDObject());
    }

    private void init(EDeliveryDetails details, REMEvidenceType submissionAcceptanceRejection) {
        evidenceType = Evidences.RELAY_REM_MD_FAILURE;

        LOG.debug("Create RelayREMMDFailure.");
        setEventCode(Evidences.RELAY_REM_MD_FAILURE.getFaultEventCode());

        initWithPrevious(submissionAcceptanceRejection);
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
                new ObjectFactory().createRelayREMMDFailure(jaxbObj),
                out
            );
    }
}
