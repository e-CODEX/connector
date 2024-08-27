/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.09.18 at 01:31:03 PM MESZ 
//

package eu.spocseu.edeliverygw.configuration.xsd;

import jakarta.xml.bind.annotation.XmlRegistry;
import lombok.NoArgsConstructor;

/**
 * This object contains factory methods for each Java content interface and Java element interface
 * generated in the eu.eu_spocs.uri.configuration.edelivery package.
 *
 * <p>An ObjectFactory allows you to programmatically
 * construct new instances of the Java representation for XML content. The Java representation of
 * XML content can consist of schema derived interfaces and classes representing the binding of
 * schema type definitions, element declarations and model groups.  Factory methods for each of
 * these are provided in this class.
 */
@XmlRegistry
@NoArgsConstructor
public class ObjectFactory {
    /**
     * Create an instance of {@link EDeliveryDetail.PostalAdress}.
     */
    public EDeliveryDetail.PostalAdress createEDeliveryDetailPostalAdress() {
        return new EDeliveryDetail.PostalAdress();
    }

    /**
     * Create an instance of {@link EDeliveryDetail}.
     */
    public EDeliveryDetail createEDeliveryDetail() {
        return new EDeliveryDetail();
    }

    /**
     * Create an instance of {@link EDeliveryDetail.Server}.
     */
    public EDeliveryDetail.Server createEDeliveryDetailServer() {
        return new EDeliveryDetail.Server();
    }
}
