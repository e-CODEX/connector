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

import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.etsi.uri._02231.v2_.ElectronicAddressType;
import org.etsi.uri._02640.v2.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2.EntityDetailsType;
import org.etsi.uri._02640.v2.EntityNameType;
import org.etsi.uri._02640.v2.NamePostalAddressType;
import org.etsi.uri._02640.v2.NamesPostalAddressListType;
import org.etsi.uri._02640.v2.PostalAddressType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.common.SpocsConstants.COUNTRY_CODES;

/**
 * This class contains several Helper Methods of the SPOCS Context.
 * 
 */
public class SpocsFragments
{
	private static Logger LOG = LoggerFactory.getLogger(SpocsFragments.class);
	
	/*
	public static ReliableMessagingFeature buildSpocsRMFheatures()
	{
		ReliableMessagingFeatureBuilder rmFeatureBuilder = new ReliableMessagingFeatureBuilder(
			RmProtocolVersion.WSRM200702);
		rmFeatureBuilder
				.retransmissionBackoffAlgorithm(BackoffAlgorithm.EXPONENTIAL);
		rmFeatureBuilder.sequenceInactivityTimeout(10000L);
		rmFeatureBuilder.maxRmSessionControlMessageResendAttempts(10);
		rmFeatureBuilder.maxMessageRetransmissionCount(10);
		//rmFeatureBuilder.closeSequenceOperationTimeout(120000L);
		//rmFeatureBuilder.securityBinding(null);

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
	// EntityDetailsType entitiyDetails = new EntityDetailsType();
	// NamesPostalAddressListType namePostalList = new
	// NamesPostalAddressListType();
	//
	// if (postalAddress != null) {
	// NamePostalAddressType postal = new NamePostalAddressType();
	// postal.setPostalAddress(postalAddress);
	// namePostalList.getNamePostalAddress().add(postal);
	// entitiyDetails.setNamesPostalAddresses(namePostalList);
	// }
	//
	// entitiyDetails.getAttributedElectronicAddressOrElectronicAddress().add(
	// electronicAddressType);
	// return entitiyDetails;
	//
	// }

	/**
	 * This Method creates an {@link EntityDetailsType} with the given
	 * Parameters.
	 * 
	 * @param postalAddress
	 * @param electronicAddressType
	 * @return {@link EntityDetailsType}
	 */
	public static EntityDetailsType createEntityDetails(
			NamePostalAddressType postalAddress,
			AttributedElectronicAddressType electronicAddressType)
	{
		EntityDetailsType entitiyDetails = new EntityDetailsType();
		NamesPostalAddressListType namePostalList = new NamesPostalAddressListType();

		if (postalAddress != null) {
			namePostalList.getNamePostalAddress().add(postalAddress);
			entitiyDetails.setNamesPostalAddresses(namePostalList);
		}

		entitiyDetails.getAttributedElectronicAddressOrElectronicAddress().add(
			electronicAddressType);
		return entitiyDetails;

	}

	/**
	 * This Method validates the given {@link ElectronicAddressType} to fit the
	 * needs of the RFC822 Format. If it is incorrect a
	 * {@link MalformedURLException} ist thrown.
	 * 
	 * 
	 * @param electronicAddress
	 * @throws MalformedURLException
	 */
	public static void validateElectronicAdress(
			AttributedElectronicAddressType electronicAddress)
			throws MalformedURLException
	{
		try {
			new InternetAddress(electronicAddress.getValue(), true);
		} catch (AddressException e) {

			throw new MalformedURLException(
				"Electronic address not valid! Address: "
						+ electronicAddress.getValue());
		}
	}

	/**
	 * 
	 * This Method creates a {@link PostalAddressType} with the given Parameter
	 * 
	 * 
	 * @param stateOrProvince
	 * @param locality
	 * @param postalCode
	 * @param country
	 * @param _streetAddress
	 * 
	 * @return {@link PostalAddressType}
	 */
	public static PostalAddressType createPostalAddress(String stateOrProvince,
			String locality, String postalCode, COUNTRY_CODES country,
			String... _streetAddress)
	{
		PostalAddressType obj = new PostalAddressType();
		obj.setStateOrProvince(stateOrProvince);
		obj.setLocality(locality);

		obj.setPostalCode(postalCode);
		obj.setCountryName(country.getCode());
		for (String street : _streetAddress) {
			obj.getStreetAddress().add(street);
		}
		return obj;

	}

	public static NamePostalAddressType createNamePostalAdress(String name)
	{
		NamePostalAddressType namePostalAdress = new NamePostalAddressType();
		EntityNameType entityNameType = new EntityNameType();
		entityNameType.getName().add(name);
		namePostalAdress.setEntityName(entityNameType);
		return namePostalAdress;
	}

	/**
	 * This Method creates a {@link ElectronicAddressType} with the given
	 * Parameter.
	 * 
	 * @param address
	 * @param displayName
	 * @throws MalformedURLException
	 * @return {@link ElectronicAddressType}
	 */
	public static AttributedElectronicAddressType createElectoricAddress(
			String address, String displayName) throws MalformedURLException
	{
		AttributedElectronicAddressType eAddre = new AttributedElectronicAddressType();
		if (displayName != null) {
			eAddre.setDisplayName(displayName);
		}
		eAddre.setValue(address);
		eAddre.setScheme("mailto");
		
		//No addressvalidation for e-CODEX because there is no format defined
//		SpocsFragments.validateElectronicAdress(eAddre);
		return eAddre;
	}

	/**
	 * This Method creates a {@link ElectronicAddressType} with the given
	 * Parameter.
	 * 
	 * @param address
	 * @return {@link ElectronicAddressType}
	 * @throws MalformedURLException
	 */

	public static AttributedElectronicAddressType createElectoricAddress(
			String address) throws MalformedURLException
	{
		return createElectoricAddress(address, null);
	}

	/**
	 * 
	 * This Method gets the first Electronic Adress where the URI is set out of
	 * a {@link EntityDetailsType}
	 * 
	 * @param jaxbObj
	 * @return
	 */

	public static AttributedElectronicAddressType getFirstElectronicAddressWithURI(
			EntityDetailsType jaxbObj)
	{

		AttributedElectronicAddressType eAdress = (AttributedElectronicAddressType) jaxbObj
				.getAttributedElectronicAddressOrElectronicAddress().get(0);
		if (eAdress.getValue() != null)
			return eAdress;
		else {

			LOG.info("Electronic Adress has no value!");
			return null;
		}

	}

	/**
	 * This Method gets the first {@link NamePostalAddressType} of a
	 * {@link EntityDetailsType}
	 * 
	 * @param jaxbObj
	 * @return
	 */

	public static NamePostalAddressType getFirstNamePostalAddressType(
			EntityDetailsType jaxbObj)
	{
		Iterator<NamePostalAddressType> it = jaxbObj.getNamesPostalAddresses()
				.getNamePostalAddress().iterator();
		while (it.hasNext()) {
			NamePostalAddressType next = it.next();
			if (next instanceof NamePostalAddressType) {
				return (NamePostalAddressType) next;
			}
		}
		LOG.warn("No NamePostalAddressType found");
		return null;
	}

	/**
	 * Creates an XMLGregorianCalendar with the given date object.
	 * 
	 * @param date
	 *            The date/time that is to be included in the
	 *            XMLGregorianCalendar.
	 * @return The created XMLGregorianCalendar object.
	 * @throws DatatypeConfigurationException
	 *             If there are converting errors with the date objects.
	 */
	public static XMLGregorianCalendar createXMLGregorianCalendar(Date date)
			throws DatatypeConfigurationException
	{
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(cal);
		return xmlCal;
	}

	/**
	 * Creates a XMLGregorianCalendar with the date/time from now plus the given
	 * days.
	 * 
	 * @param deltaDays
	 *            The count of days to add to the current date/time.
	 * @return The created XMLGregorianCalendar object.
	 * @throws DatatypeConfigurationException
	 *             If there are converting errors with the date objects.
	 */
	public static XMLGregorianCalendar createXMLGregorianCalendar(int deltaDays)
			throws DatatypeConfigurationException
	{
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)
				+ deltaDays);
		XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(cal);
		return xmlCal;
	}
	
	public static AttributedElectronicAddressType getAttributedElectronicAdress(
			EntityDetailsType details)
	{
		List<Object> elecAdress = details
				.getAttributedElectronicAddressOrElectronicAddress();
		for (Object object : elecAdress) {
			if (object instanceof AttributedElectronicAddressType) {
				return (AttributedElectronicAddressType) object;
			}
		}
		return null;
	}

}
