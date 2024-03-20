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

import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

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

import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;

/**
 * Internal class
 * 
 * @author Lindemann
 * 
 */
public abstract class Evidence
{
	private static Logger LOG = LoggerFactory.getLogger(Evidence.class);
	protected REMEvidenceType jaxbObj;
	protected Evidences evidenceType;
	protected EDeliveryDetails details;
	
	protected Evidence(REMEvidenceType _jaxbObj)
	{
		jaxbObj = _jaxbObj;
	}

	protected Evidence()
	{
	}

	protected Evidence(EDeliveryDetails _details)
	{
		initEvidenceIssuerDetailsWithEdeliveryDetails(_details);
	}
	
	protected void initEvidenceIssuerDetailsWithEdeliveryDetails(EDeliveryDetails _details) {
		details = _details;
		jaxbObj = new REMEvidenceType();
		jaxbObj.setVersion("2.1.1");
		EvidenceIssuerPolicyIDType issuerPolicy = new EvidenceIssuerPolicyIDType();
		issuerPolicy.getPolicyID()
				.add("http://uri.eu-ecodex.eu/eDeliveryPolicy");

		jaxbObj.setEvidenceIssuerPolicyID(issuerPolicy);
		jaxbObj.setEvidenceIdentifier(UUID.randomUUID().toString());

		EntityDetailsType issuerDetails = createEntityDetailsType(
			null, details.getGatewayName(),
			details.getStreetAdress(), details.getLocality(),
			details.getPostalCode(), details.getCountry(),
			details.getGatewayName());

		AttributedElectronicAddressType elAddre = new AttributedElectronicAddressType();
		elAddre.setValue(details.getGatewayAddress());
		elAddre.setScheme("mailto");
		issuerDetails.getAttributedElectronicAddressOrElectronicAddress().add(elAddre);
		jaxbObj.setEvidenceIssuerDetails(issuerDetails);
		try {
			jaxbObj.setEventTime(SpocsFragments
					.createXMLGregorianCalendar(new Date()));
		} catch (DatatypeConfigurationException e) {
			LOG.error("Date error:" + e.getMessage());
		}
	}

	protected void initWithPrevious(REMEvidenceType previousJaxB)
	{
		// set the sender details
		jaxbObj.setSenderDetails(previousJaxB.getSenderDetails());
		// set the recipients details
		jaxbObj.setRecipientsDetails(previousJaxB.getRecipientsDetails());

		// jaxbObj.setSubmissionTime(submissionAcceptanceRejection.getMetaData()
		// .getDeliveryConstraints().getInitialSend());

		jaxbObj.setReplyToAddress(previousJaxB.getReplyToAddress());
		jaxbObj.setSenderMessageDetails(previousJaxB.getSenderMessageDetails());

	}

	public void setEventCode(String _eventCode)
	{
		jaxbObj.setEventCode(_eventCode);

	}

	public Evidences getEvidenceType()
	{
		return evidenceType;
	}

	public void setEvidenceType(Evidences evidenceType)
	{
		this.evidenceType = evidenceType;

	}

	public REMEvidenceType getXSDObject()
	{
		return jaxbObj;
	}
	
	public void setEventReason(EventReasonType eventReasonType)
	{
		EventReasonsType eventResonsType = new EventReasonsType();

		eventResonsType.getEventReason().add(eventReasonType);
		
		jaxbObj.setEventReasons(eventResonsType);
	}
	
	public EventReasonType getEventReson()
	{
		if (jaxbObj.getEventReasons() == null)
			return null;
		else
		{
			List<EventReasonType> reasons = jaxbObj.getEventReasons().getEventReason();
			
			if(reasons != null && reasons.size() == 1)
				return reasons.get(0);
			else
				return null;
		}
	}
	
	public void setUAMessageId(String id) {
		jaxbObj.getSenderMessageDetails().setUAMessageIdentifier(id);
	}
	
	public void setHashInformation(byte[] hashValue, String hashAlgorithm) {
		jaxbObj.getSenderMessageDetails().setDigestValue(hashValue);
		DigestMethodType methodType = new DigestMethodType();
		methodType.setAlgorithm(hashAlgorithm);
		jaxbObj.getSenderMessageDetails().setDigestMethod(methodType);
		
	}

	protected REMEvidenceType createRemEvidenceType(EDeliveryDetails details,
			String senderEAddress, String recipientAddress)
	{
		REMEvidenceType remEvi = new REMEvidenceType();
		jaxbObj.setVersion("1.0");
		remEvi.setEvidenceIdentifier("uuid:" + UUID.randomUUID().toString());

		remEvi.setEvidenceIssuerDetails(createEntityDetailsType(
				details.getGatewayAddress(), 
				details.getGatewayName(), 
				details.getStreetAdress(), 
				details.getLocality(), 
				details.getPostalCode(), 
				details.getCountry(), 
				details.getGatewayName()));
		try {
			remEvi.setEventTime(SpocsFragments
					.createXMLGregorianCalendar(new Date()));
		} catch (DatatypeConfigurationException e) {
			LOG.error("Date error:" + e.getMessage());
		}
		remEvi.setSenderDetails(createEntityDetailsType(senderEAddress));
		EntityDetailsListType detailList = new EntityDetailsListType();
		detailList.getEntityDetails().add(
			createEntityDetailsType(recipientAddress));
		remEvi.setRecipientsDetails(detailList);

		return remEvi;
	}

	protected EntityDetailsType createEntityDetailsType(String eAddress)
	{

		return createEntityDetailsType(eAddress, null, null, null, null, null,
			(String) null);
	}

	protected EntityDetailsType createEntityDetailsType(String eAddress,
			String displayName, String postalName)
	{
		String[] array = { postalName };
		return createEntityDetailsType(eAddress, displayName, null, null, null,
			null, array);
	}

	protected EntityDetailsType createEntityDetailsType(String eAddress,
			String displayName, String[] postalName)
	{
		return createEntityDetailsType(eAddress, displayName, null, null, null,
			null, postalName);
	}

	protected EntityDetailsType createEntityDetailsType(String eAddress,
			String displayName, String street, String locality, String zipcode,
			String country, String postalName)
	{
		String[] array = { postalName };
		return createEntityDetailsType(eAddress, displayName, street, locality,
			zipcode, country, array);
	}

	protected EntityDetailsType createEntityDetailsType(String eAddress,
			String displayName, String street, String locality, String zipcode,
			String country, String[] postalName)
	{

		// prepare
		EntityDetailsType detailsType = new EntityDetailsType();

		// set the values
		AttributedElectronicAddressType elAddre = new AttributedElectronicAddressType();

		if (postalName != null) {
			NamePostalAddressType postAddre = new NamePostalAddressType();
			EntityNameType name = new EntityNameType();
			for (String string : postalName) {
				name.getName().add(string);
			}
			postAddre.setEntityName(name);

			PostalAddressType postalAddressType = new PostalAddressType();
			if (street != null)
				postalAddressType.getStreetAddress().add(street);
			if (locality != null) postalAddressType.setLocality(locality);
			if (country != null) postalAddressType.setCountryName(country);
			if (zipcode != null) postalAddressType.setPostalCode(zipcode);

			if (street != null || locality != null || zipcode != null) {
				postAddre.setPostalAddress(postalAddressType);

			}
			NamesPostalAddressListType postList = new NamesPostalAddressListType();
			postList.getNamePostalAddress().add(postAddre);
			detailsType.setNamesPostalAddresses(postList);
		}

		if (displayName != null) elAddre.setDisplayName(displayName);
		if (eAddress != null) {
			elAddre.setValue(eAddress);
			// elAddre.setScheme(SpocsConstants.E_ADDRESS_SCHEMES.RFC5322ADDRESS
			// .name());
			detailsType.getAttributedElectronicAddressOrElectronicAddress()
					.add(elAddre);
		}
		return detailsType;
	}

	protected ElectronicAddressType createElectronicAddressType(String eAddress)
	{
		ElectronicAddressType elecAddressType = new ElectronicAddressType();
		elecAddressType.getURI().add(eAddress);
		return elecAddressType;
	}

	protected NamesPostalAddressListType createPostalAddress(String name)
	{
		if (name == null) return null;
		NamesPostalAddressListType postAddresses = new NamesPostalAddressListType();
		NamePostalAddressType address = new NamePostalAddressType();
		EntityNameType entityName = new EntityNameType();
		entityName.getName().add(name);
		address.setEntityName(entityName);
		postAddresses.getNamePostalAddress().add(address);
		return postAddresses;
	}

	public abstract void serialize(OutputStream out) throws JAXBException;
	
		
}
