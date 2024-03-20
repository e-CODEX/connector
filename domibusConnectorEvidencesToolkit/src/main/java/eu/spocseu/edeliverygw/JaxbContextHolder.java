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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * This class represents a Holder for the adressing, spocs and etsi JAXB
 * Context.
 * 
 * @author R. Lindemann
 */
public class JaxbContextHolder
{

	private static javax.xml.bind.JAXBContext spocsContext = null;
	private static javax.xml.bind.JAXBContext soapContext = null;
	private static javax.xml.bind.JAXBContext addressingContext = null;

	private static javax.xml.bind.JAXBContext etsi_vi = null;

	/**
	 * Internal method to get the JAXB context to marshal and unmarshal spocs
	 * objects.
	 * 
	 * @return The created JAXB context.
	 * @throws JAXBException
	 *             In case of errors creating the JAXB context.
	 */
	public static javax.xml.bind.JAXBContext getSpocsJaxBContext()
			throws JAXBException
	{
		if (spocsContext == null) {
			spocsContext = JAXBContext
					.newInstance(org.etsi.uri._02640.v2.ObjectFactory.class, org.etsi.uri._02640.soapbinding.v1_.ObjectFactory.class);
		}
		return spocsContext;
	}
	
	
	public static javax.xml.bind.JAXBContext getSoapBindingJaxBContext()
			throws JAXBException
	{
		if (soapContext == null) {
			soapContext = JAXBContext
					.newInstance(org.etsi.uri._02640.soapbinding.v1_.ObjectFactory.class);
		}
		return soapContext;
	}

	/**
	 * Internal method to get the JAXB context to marshal and unmarshal
	 * addressing objects.
	 * 
	 * @return The created JAXB context.
	 * @throws JAXBException
	 *             In case of errors creating the JAXB context.
	 */
	/*
	public static javax.xml.bind.JAXBContext getAddressingJaxBContext()
			throws JAXBException
	{
		if (addressingContext == null) {
			addressingContext = JAXBContext.newInstance(ObjectFactory.class);
		}
		return addressingContext;
	}
	*/

	/**
	 * Internal method to get the JAXB context to marshal and unmarshal etsiV1
	 * objects.
	 * 
	 * @return The created JAXB context.
	 * @throws JAXBException
	 *             In case of errors creating the JAXB context.
	 */
	public static javax.xml.bind.JAXBContext getETSIV2JaxBContext()
			throws JAXBException
	{
		if (etsi_vi == null) {
			etsi_vi = JAXBContext
					.newInstance(org.etsi.uri._02640.v2.ObjectFactory.class);
		}
		return etsi_vi;
	}
}
