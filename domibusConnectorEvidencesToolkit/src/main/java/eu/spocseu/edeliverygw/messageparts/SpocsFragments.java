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

package eu.spocseu.edeliverygw.messageparts;

import eu.spocseu.common.SpocsConstants.COUNTRY_CODES;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.experimental.UtilityClass;
import org.etsi.uri._02231.v2_.ElectronicAddressType;
import org.etsi.uri._02640.v2.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2.EntityDetailsType;
import org.etsi.uri._02640.v2.EntityNameType;
import org.etsi.uri._02640.v2.NamePostalAddressType;
import org.etsi.uri._02640.v2.NamesPostalAddressListType;
import org.etsi.uri._02640.v2.PostalAddressType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains several Helper Methods of the SPOCS Context.
 */
@UtilityClass
public class SpocsFragments {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpocsFragments.class);
    /*
    public static ReliableMessagingFeature buildSpocsRMFheatures() {
        ReliableMessagingFeatureBuilder rmFeatureBuilder = new ReliableMessagingFeatureBuilder(
            RmProtocolVersion.WSRM200702);
        rmFeatureBuilder.retransmissionBackoffAlgorithm(BackoffAlgorithm.EXPONENTIAL);
        rmFeatureBuilder.sequenceInactivityTimeout(10000L);
        rmFeatureBuilder.maxRmSessionControlMessageResendAttempts(10);
        rmFeatureBuilder.maxMessageRetransmissionCount(10);
        // rmFeatureBuilder.closeSequenceOperationTimeout(120000L);
        // rmFeatureBuilder.securityBinding(null);
        return rmFeatureBuilder.build();
    }
    */

    // /**
    // * This Method creates an {@link EDeliveryActorType} with the given
    // * Parameters.
    // *
    // * @param postalAddress
    // * @param electronicAddressType
    // * @return {@link EDeliveryActorType}
    // */
    // public static EntityDetailsType createEntityDetails(
    // PostalAddressType postalAddress,
    // AttributedElectronicAddressType electronicAddressType)
    // {
    // EntityDetailsType     entityDetails = new EntityDetailsType();
    // NamesPostalAddressListType namePostalList = new
    // NamesPostalAddressListType();
    //
    // if (postalAddress != null) {
    // NamePostalAddressType postal = new NamePostalAddressType();
    // postal.setPostalAddress(postalAddress);
    // namePostalList.getNamePostalAddress().add(postal);
    //     entityDetails.setNamesPostalAddresses(namePostalList);
    // }
    //
    //     entityDetails.getAttributedElectronicAddressOrElectronicAddress().add(
    // electronicAddressType);
    // return entityDetails;
    //
    // }

    /**
     * Creates an instance of EntityDetailsType with the given postal address and electronic
     * address.
     *
     * @param postalAddress         The NamePostalAddressType object representing the postal
     *                              address.
     * @param electronicAddressType The AttributedElectronicAddressType object representing the
     *                              electronic address.
     * @return The created EntityDetailsType object.
     */
    public static EntityDetailsType createEntityDetails(
        NamePostalAddressType postalAddress,
        AttributedElectronicAddressType electronicAddressType) {
        var entityDetails = new EntityDetailsType();
        var namePostalList = new NamesPostalAddressListType();

        if (postalAddress != null) {
            namePostalList.getNamePostalAddress().add(postalAddress);
            entityDetails.setNamesPostalAddresses(namePostalList);
        }

        entityDetails.getAttributedElectronicAddressOrElectronicAddress().add(
            electronicAddressType);
        return entityDetails;
    }

    /**
     * This Method validates the given {@link ElectronicAddressType} to fit the needs of the
     * RFC822.
     *
     * @param electronicAddress The electronic address to validate.
     * @throws MalformedURLException If the electronic address is not valid.
     */
    public static void validateElectronicAddress(
        AttributedElectronicAddressType electronicAddress)
        throws MalformedURLException {
        try {
            new InternetAddress(electronicAddress.getValue(), true);
        } catch (AddressException e) {

            throw new MalformedURLException(
                "Electronic address not valid! Address: "
                    + electronicAddress.getValue());
        }
    }

    /**
     * Creates a PostalAddressType object with the provided address details.
     *
     * @param stateOrProvince The state or province of the postal address.
     * @param locality        The locality or city of the postal address.
     * @param postalCode      The postal code of the address.
     * @param country         The country code of the address.
     * @param streetAddress   Any additional street addresses.
     * @return The created PostalAddressType object.
     */
    public static PostalAddressType createPostalAddress(
        String stateOrProvince,
        String locality, String postalCode, COUNTRY_CODES country,
        String... streetAddress) {
        var postalAddressType = new PostalAddressType();
        postalAddressType.setStateOrProvince(stateOrProvince);
        postalAddressType.setLocality(locality);

        postalAddressType.setPostalCode(postalCode);
        postalAddressType.setCountryName(country.getCode());
        for (String street : streetAddress) {
            postalAddressType.getStreetAddress().add(street);
        }
        return postalAddressType;
    }

    /**
     * Creates a NamePostalAddressType object with the given name.
     *
     * @param name The name to be added to the NamePostalAddressType.
     * @return The created NamePostalAddressType object with the provided name.
     */
    public static NamePostalAddressType createNamePostalAddress(String name) {
        var namePostalAddress = new NamePostalAddressType();
        var entityNameType = new EntityNameType();
        entityNameType.getName().add(name);
        namePostalAddress.setEntityName(entityNameType);
        return namePostalAddress;
    }

    /**
     * Creates an AttributedElectronicAddressType object with the given address and display name.
     *
     * @param address     The email address.
     * @param displayName The display name for the email address.
     * @return The created AttributedElectronicAddressType object.
     * @throws MalformedURLException If the address is not a valid email address.
     */
    public static AttributedElectronicAddressType createElectronicAddress(
        String address, String displayName) throws MalformedURLException {
        var electronicAddress = new AttributedElectronicAddressType();
        if (displayName != null) {
            electronicAddress.setDisplayName(displayName);
        }
        electronicAddress.setValue(address);
        electronicAddress.setScheme("mailto");

        // No addressValidation for e-CODEX because there is no format defined
        // SpocsFragments.validateElectronicAddress(eAddress);
        return electronicAddress;
    }

    /**
     * Creates an AttributedElectronicAddressType object with the given address and display name.
     *
     * @param address The email address.
     * @return The created AttributedElectronicAddressType object.
     * @throws MalformedURLException If the address is not a valid email address.
     */
    public static AttributedElectronicAddressType createElectronicAddress(
        String address) throws MalformedURLException {
        return createElectronicAddress(address, null);
    }

    /**
     * This Method gets the first Electronic Address where the URI is set out of a *
     * {@link EntityDetailsType}.
     *
     * @param jaxbObj The {@link EntityDetailsType} object from which to retrieve the electronic
     *                address.
     * @return The first instance of {@link AttributedElectronicAddressType} with a URI value found
     *      in the {@link EntityDetailsType}, or null if no such instance is found.
     */
    public static AttributedElectronicAddressType getFirstElectronicAddressWithURI(
        EntityDetailsType jaxbObj) {

        AttributedElectronicAddressType electronicAddressType =
            (AttributedElectronicAddressType) jaxbObj
                .getAttributedElectronicAddressOrElectronicAddress().getFirst();
        if (electronicAddressType.getValue() != null) {
            return electronicAddressType;
        } else {

            LOGGER.info("Electronic Address has no value!");
            return null;
        }
    }

    /**
     * Returns the first {@link NamePostalAddressType} of a {@link EntityDetailsType}.
     *
     * @param jaxbObj The EntityDetailsType object from which to retrieve the
     *                NamePostalAddressType.
     * @return The first NamePostalAddressType object if found, null otherwise.
     */
    public static NamePostalAddressType getFirstNamePostalAddressType(
        EntityDetailsType jaxbObj) {
        for (NamePostalAddressType next : jaxbObj.getNamesPostalAddresses()
                                                 .getNamePostalAddress()) {
            if (next instanceof NamePostalAddressType namePostalAddressType) {
                return namePostalAddressType;
            }
        }
        LOGGER.warn("No NamePostalAddressType found");
        return null;
    }

    /**
     * Creates an XMLGregorianCalendar with the given date object.
     *
     * @param date The date/time that is to be included in the XMLGregorianCalendar.
     * @return The created XMLGregorianCalendar object.
     * @throws DatatypeConfigurationException If there are converting errors with the date objects.
     */
    public static XMLGregorianCalendar createXMLGregorianCalendar(Date date)
        throws DatatypeConfigurationException {
        var cal = new GregorianCalendar();
        cal.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    }

    /**
     * Creates a XMLGregorianCalendar with the date/time from now plus the given days.
     *
     * @param deltaDays The count of days to add to the current date/time.
     * @return The created XMLGregorianCalendar object.
     * @throws DatatypeConfigurationException If there are converting errors with the date objects.
     */
    public static XMLGregorianCalendar createXMLGregorianCalendar(int deltaDays)
        throws DatatypeConfigurationException {
        var cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)
            + deltaDays);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    }

    /**
     * Retrieves the first AttributedElectronicAddressType object from the given EntityDetailsType.
     *
     * @param details The EntityDetailsType object containing electronic addresses.
     * @return The first instance of AttributedElectronicAddressType found in the EntityDetailsType,
     *      or null if no such instance is found.
     */
    public static AttributedElectronicAddressType getAttributedElectronicAddress(
        EntityDetailsType details) {
        List<Object> electronicAddress = details
            .getAttributedElectronicAddressOrElectronicAddress();
        for (Object object : electronicAddress) {
            if (object instanceof AttributedElectronicAddressType electronicAddressType) {
                return electronicAddressType;
            }
        }
        return null;
    }
}
