
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <p>{@literal
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="Server">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;attribute name="defaultCitizenQAAlevel" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                   &lt;attribute name="gatewayName" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                   &lt;attribute name="gatewayDomain" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                   &lt;attribute name="gatewayAddress" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="Client" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;/choice>
 *         &lt;element name="PostalAdress">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="StreetAddress" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                 &lt;attribute name="Locality" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                 &lt;attribute name="PostalCode" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                 &lt;attribute name="Country" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * }
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "", propOrder = {
    "server",
    "client",
    "postalAdress"
}
)
@XmlRootElement(name = "EDeliveryDetail")
@Getter
@Setter
public class EDeliveryDetail {
    @XmlElement(name = "Server")
    protected EDeliveryDetail.Server server;
    @XmlElement(name = "Client")
    protected Object client;
    @XmlElement(name = "PostalAdress", required = true)
    protected EDeliveryDetail.PostalAdress postalAdress;

    /**
     * Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <p>{@literal
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="StreetAddress" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *       &lt;attribute name="Locality" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *       &lt;attribute name="PostalCode" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *       &lt;attribute name="Country" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * }
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    @Getter
    @Setter
    public static class PostalAdress {
        @XmlAttribute(name = "StreetAddress")
        @XmlSchemaType(name = "anySimpleType")
        protected String streetAddress;
        @XmlAttribute(name = "Locality")
        @XmlSchemaType(name = "anySimpleType")
        protected String locality;
        @XmlAttribute(name = "PostalCode")
        @XmlSchemaType(name = "anySimpleType")
        protected String postalCode;
        @XmlAttribute(name = "Country")
        @XmlSchemaType(name = "anySimpleType")
        protected String country;
    }

    /**
     * Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <p>{@literal
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="defaultCitizenQAAlevel" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="gatewayName" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *       &lt;attribute name="gatewayDomain" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *       &lt;attribute name="gatewayAddress" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * }
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    @Getter
    @Setter
    public static class Server {
        @XmlAttribute
        protected Integer defaultCitizenQAAlevel;
        @XmlAttribute(required = true)
        @XmlSchemaType(name = "anySimpleType")
        protected String gatewayName;
        @XmlAttribute
        @XmlSchemaType(name = "anySimpleType")
        protected String gatewayDomain;
        @XmlAttribute(required = true)
        @XmlSchemaType(name = "anySimpleType")
        protected String gatewayAddress;
    }
}
