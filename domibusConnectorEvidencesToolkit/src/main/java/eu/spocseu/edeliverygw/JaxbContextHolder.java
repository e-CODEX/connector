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
$Date: 2010-10-14 18:55:57 +0200 (Do, 14. Okt 2010) $
$Revision: 86 $

See SPOCS_WP3_LICENSE_URL for license information
--------------------------------------------------------------------------- */

package eu.spocseu.edeliverygw;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.experimental.UtilityClass;

/**
 * This class represents a Holder for the addressing, spocs and etsi JAXB Context.
 *
 * @author R. Lindemann
 */
@UtilityClass
public class JaxbContextHolder {
    private static jakarta.xml.bind.JAXBContext spocsContext = null;
    private static jakarta.xml.bind.JAXBContext soapContext = null;
    private static jakarta.xml.bind.JAXBContext addressingContext = null;
    private static jakarta.xml.bind.JAXBContext etsi_vi = null;

    /**
     * Internal method to get the JAXB context to marshal and unmarshal spocs objects.
     *
     * @return The created JAXB context.
     * @throws JAXBException In case of errors creating the JAXB context.
     */
    public static jakarta.xml.bind.JAXBContext getSpocsJaxBContext()
        throws JAXBException {
        if (spocsContext == null) {
            spocsContext = JAXBContext
                .newInstance(
                    org.etsi.uri._02640.v2.ObjectFactory.class,
                    org.etsi.uri._02640.soapbinding.v1_.ObjectFactory.class
                );
        }
        return spocsContext;
    }

    /**
     * Retrieves the JAXB context for soapbinding objects.
     *
     * @return The JAXB context for soapbinding objects.
     * @throws JAXBException In case of errors creating the JAXB context.
     */
    public static jakarta.xml.bind.JAXBContext getSoapBindingJaxBContext()
        throws JAXBException {
        if (soapContext == null) {
            soapContext = JAXBContext
                .newInstance(org.etsi.uri._02640.soapbinding.v1_.ObjectFactory.class);
        }
        return soapContext;
    }

    /*
    /**
     * Internal method to get the JAXB context to marshal and unmarshal addressing objects.
     *
     * @return The created JAXB context.
     * @throws JAXBException In case of errors creating the JAXB context.
     */
    /*
    public static javax.xml.bind.JAXBContext getAddressingJaxBContext()
        throws JAXBException {
        if (addressingContext == null) {
            addressingContext = JAXBContext.newInstance(ObjectFactory.class);
        }
        return addressingContext;
    }
    */

    /**
     * Internal method to get the JAXB context to marshal and unmarshal etsiV1 objects.
     *
     * @return The created JAXB context.
     * @throws JAXBException In case of errors creating the JAXB context.
     */
    public static jakarta.xml.bind.JAXBContext getETSIV2JaxBContext()
        throws JAXBException {
        if (etsi_vi == null) {
            etsi_vi = JAXBContext
                .newInstance(org.etsi.uri._02640.v2.ObjectFactory.class);
        }
        return etsi_vi;
    }
}
