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

import eu.spocseu.edeliverygw.JaxbContextHolder;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import jakarta.xml.bind.JAXBException;
import java.io.OutputStream;
import org.etsi.uri._02640.v2.ObjectFactory;
import org.etsi.uri._02640.v2.REMEvidenceType;

/**
 * This class represents a ReceivedByNonREMSystem evidence. It helps to create the underlying
 * REMEvidenceType JAXB object of the xsd structure.
 *
 * @author Lindemann
 */
public class ReceivedByNonREMSystem extends Evidence {
    /**
     * This constructor creates this ReceivedByNonREMSystem evidence.
     *
     * @param details Configuration object to set some properties
     */
    public ReceivedByNonREMSystem(EDeliveryDetails details) {
        super(details);
    }

    /**
     * This constructor creates this ReceivedByNonREMSystem evidence with the given JAXB object and
     * the configuration.
     *
     * @param evidenceType The JAXB object.
     */
    public ReceivedByNonREMSystem(REMEvidenceType evidenceType) {
        super(evidenceType);
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
                new ObjectFactory().createReceivedFromNonREMSystem(jaxbObj),
                out
            );
    }
}
