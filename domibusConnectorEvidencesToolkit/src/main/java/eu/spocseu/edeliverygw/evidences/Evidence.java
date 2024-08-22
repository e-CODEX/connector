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
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;
import jakarta.xml.bind.JAXBException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.xml.datatype.DatatypeConfigurationException;
import lombok.Getter;
import lombok.Setter;
import org.etsi.uri._02231.v2_.ElectronicAddressType;
import org.etsi.uri._02640.v2.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2.EntityDetailsListType;
import org.etsi.uri._02640.v2.EntityDetailsType;
import org.etsi.uri._02640.v2.EntityNameType;
import org.etsi.uri._02640.v2.EventReasonType;
import org.etsi.uri._02640.v2.EventReasonsType;
import org.etsi.uri._02640.v2.EvidenceIssuerPolicyIDType;
import org.etsi.uri._02640.v2.NamePostalAddressType;
import org.etsi.uri._02640.v2.NamesPostalAddressListType;
import org.etsi.uri._02640.v2.PostalAddressType;
import org.etsi.uri._02640.v2.REMEvidenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2000._09.xmldsig_.DigestMethodType;

/**
 * The Evidence class is an abstract class that represents evidence for a specific event. It
 * provides methods for setting various properties of the evidence such as event code, event type,
 * event reason, and message ID. It also provides methods for creating and initializing different
 * types of entity details.
 *
 * @author Lindemann
 */
@Getter
@Setter
public abstract class Evidence {
    private static final Logger LOG = LoggerFactory.getLogger(Evidence.class);
    protected REMEvidenceType jaxbObj;
    protected Evidences evidenceType;
    protected EDeliveryDetails details;

    protected Evidence(REMEvidenceType jaxbObj) {
        this.jaxbObj = jaxbObj;
    }

    protected Evidence() {
    }

    protected Evidence(EDeliveryDetails details) {
        initEvidenceIssuerDetailsWithEdeliveryDetails(details);
    }

    protected void initEvidenceIssuerDetailsWithEdeliveryDetails(EDeliveryDetails details) {
        this.details = details;
        jaxbObj = new REMEvidenceType();
        jaxbObj.setVersion("2.1.1");
        var issuerPolicy = new EvidenceIssuerPolicyIDType();
        issuerPolicy.getPolicyID().add("http://uri.eu-ecodex.eu/eDeliveryPolicy");

        jaxbObj.setEvidenceIssuerPolicyID(issuerPolicy);
        jaxbObj.setEvidenceIdentifier(UUID.randomUUID().toString());

        var issuerDetails = createEntityDetailsType(
            null,
            this.details.getGatewayName(),
            this.details.getStreetAdress(),
            this.details.getLocality(),
            this.details.getPostalCode(),
            this.details.getCountry(),
            this.details.getGatewayName()
        );

        var attributedElectronicAddressType = new AttributedElectronicAddressType();
        attributedElectronicAddressType.setValue(this.details.getGatewayAddress());
        attributedElectronicAddressType.setScheme("mailto");
        issuerDetails
            .getAttributedElectronicAddressOrElectronicAddress()
            .add(attributedElectronicAddressType);
        jaxbObj.setEvidenceIssuerDetails(issuerDetails);
        try {
            jaxbObj.setEventTime(SpocsFragments
                                     .createXMLGregorianCalendar(new Date()));
        } catch (DatatypeConfigurationException e) {
            LOG.error("Date error: {}", e.getMessage());
        }
    }

    protected void initWithPrevious(REMEvidenceType previousJaxB) {
        // set the sender details
        jaxbObj.setSenderDetails(previousJaxB.getSenderDetails());
        // set the recipients details
        jaxbObj.setRecipientsDetails(previousJaxB.getRecipientsDetails());

        // jaxbObj.setSubmissionTime(submissionAcceptanceRejection.getMetaData()
        // .getDeliveryConstraints().getInitialSend());

        jaxbObj.setReplyToAddress(previousJaxB.getReplyToAddress());
        jaxbObj.setSenderMessageDetails(previousJaxB.getSenderMessageDetails());
    }

    public void setEventCode(String eventCode) {
        jaxbObj.setEventCode(eventCode);
    }

    public REMEvidenceType getXSDObject() {
        return jaxbObj;
    }

    /**
     * Set the event reason for the evidence.
     *
     * @param eventReasonType The event reason type to set.
     */
    public void setEventReason(EventReasonType eventReasonType) {
        var eventResonsType = new EventReasonsType();

        eventResonsType.getEventReason().add(eventReasonType);

        jaxbObj.setEventReasons(eventResonsType);
    }

    /**
     * Retrieves the event reason for the evidence.
     *
     * @return The event reason, or null if not available.
     */
    public EventReasonType getEventReason() {
        if (jaxbObj.getEventReasons() == null) {
            return null;
        } else {
            List<EventReasonType> reasons = jaxbObj.getEventReasons().getEventReason();

            if (reasons != null && reasons.size() == 1) {
                return reasons.getFirst();
            } else {
                return null;
            }
        }
    }

    public void setUAMessageId(String id) {
        jaxbObj.getSenderMessageDetails().setUAMessageIdentifier(id);
    }

    /**
     * Sets the hash information for the evidence.
     *
     * @param hashValue     The byte array representing the hash value.
     * @param hashAlgorithm The algorithm used to calculate the hash value.
     */
    public void setHashInformation(byte[] hashValue, String hashAlgorithm) {
        jaxbObj.getSenderMessageDetails().setDigestValue(hashValue);
        var methodType = new DigestMethodType();
        methodType.setAlgorithm(hashAlgorithm);
        jaxbObj.getSenderMessageDetails().setDigestMethod(methodType);
    }

    protected REMEvidenceType createRemEvidenceType(
        EDeliveryDetails details,
        String senderEAddress, String recipientAddress) {
        var remEvi = new REMEvidenceType();
        jaxbObj.setVersion("1.0");
        remEvi.setEvidenceIdentifier("uuid:" + UUID.randomUUID());

        remEvi.setEvidenceIssuerDetails(createEntityDetailsType(
            details.getGatewayAddress(),
            details.getGatewayName(),
            details.getStreetAdress(),
            details.getLocality(),
            details.getPostalCode(),
            details.getCountry(),
            details.getGatewayName()
        ));
        try {
            remEvi.setEventTime(SpocsFragments
                                    .createXMLGregorianCalendar(new Date()));
        } catch (DatatypeConfigurationException e) {
            LOG.error("Date error: {}", e.getMessage());
        }
        remEvi.setSenderDetails(createEntityDetailsType(senderEAddress));
        var detailList = new EntityDetailsListType();
        detailList.getEntityDetails().add(
            createEntityDetailsType(recipientAddress));
        remEvi.setRecipientsDetails(detailList);

        return remEvi;
    }

    protected EntityDetailsType createEntityDetailsType(String electronicAddress) {
        return createEntityDetailsType(
            electronicAddress, null, null, null, null, null, (String) null
        );
    }

    protected EntityDetailsType createEntityDetailsType(
        String electronicAddress, String displayName, String postalName) {
        String[] array = {postalName};
        return createEntityDetailsType(electronicAddress, displayName, null, null, null,
                                       null, array
        );
    }

    protected EntityDetailsType createEntityDetailsType(
        String electronicAddress, String displayName, String[] postalName) {
        return createEntityDetailsType(
            electronicAddress, displayName, null, null, null, null, postalName
        );
    }

    protected EntityDetailsType createEntityDetailsType(
        String electronicAddress, String displayName, String street, String locality,
        String zipcode, String country, String postalName) {
        String[] array = {postalName};
        return createEntityDetailsType(electronicAddress, displayName, street, locality,
                                       zipcode, country, array
        );
    }

    protected EntityDetailsType createEntityDetailsType(
        String electronicAddress,
        String displayName, String street, String locality, String zipcode,
        String country, String[] postalName) {

        // prepare
        var detailsType = new EntityDetailsType();

        // set the values
        var elAddre = new AttributedElectronicAddressType();

        if (postalName != null) {
            var postAddre = new NamePostalAddressType();
            var name = new EntityNameType();
            for (String string : postalName) {
                name.getName().add(string);
            }
            postAddre.setEntityName(name);

            var postalAddressType = new PostalAddressType();
            if (street != null) {
                postalAddressType.getStreetAddress().add(street);
            }
            if (locality != null) {
                postalAddressType.setLocality(locality);
            }
            if (country != null) {
                postalAddressType.setCountryName(country);
            }
            if (zipcode != null) {
                postalAddressType.setPostalCode(zipcode);
            }

            if (street != null || locality != null || zipcode != null) {
                postAddre.setPostalAddress(postalAddressType);
            }
            var postList = new NamesPostalAddressListType();
            postList.getNamePostalAddress().add(postAddre);
            detailsType.setNamesPostalAddresses(postList);
        }

        if (displayName != null) {
            elAddre.setDisplayName(displayName);
        }
        if (electronicAddress != null) {
            elAddre.setValue(electronicAddress);
            // elAddre.setScheme(SpocsConstants.E_ADDRESS_SCHEMES.RFC5322ADDRESS
            // .name());
            detailsType.getAttributedElectronicAddressOrElectronicAddress()
                       .add(elAddre);
        }
        return detailsType;
    }

    protected ElectronicAddressType createElectronicAddressType(String electronicAddress) {
        var electronicAddressType = new ElectronicAddressType();
        electronicAddressType.getURI().add(electronicAddress);
        return electronicAddressType;
    }

    protected NamesPostalAddressListType createPostalAddress(String name) {
        if (name == null) {
            return null;
        }
        var postAddresses = new NamesPostalAddressListType();
        var address = new NamePostalAddressType();
        var entityName = new EntityNameType();
        entityName.getName().add(name);
        address.setEntityName(entityName);
        postAddresses.getNamePostalAddress().add(address);
        return postAddresses;
    }

    public abstract void serialize(OutputStream out) throws JAXBException;
}
