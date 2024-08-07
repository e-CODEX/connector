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

import java.io.InputStream;
import java.io.OutputStream;

import jakarta.xml.bind.JAXBException;

import org.etsi.uri._02640.v2.EventReasonType;
import org.etsi.uri._02640.v2.ObjectFactory;
import org.etsi.uri._02640.v2.REMEvidenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.edeliverygw.JaxbContextHolder;
import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;

/**
 * This class represents a RetrievalNonRetrievalByRecipient evidence. It helps
 * to create the underlying REMEvidenceType JAXB object of the xsd structure.
 * 
 * @author Lindemann
 * 
 */
public class RetrievalNonRetrievalByRecipient extends Evidence
{

	private static Logger LOG = LoggerFactory
			.getLogger(RetrievalNonRetrievalByRecipient.class);

	private boolean isSuccessful;


	/**
	 * This constructor creates this RetrievalNonRetrievalByRecipient evidence
	 * with the given JAXB object and the configuration.
	 * 
	 * @param evidenceType
	 *            The JAXB object.
	 */
	public RetrievalNonRetrievalByRecipient(REMEvidenceType evidenceType)
	{
		super(evidenceType);

	}

	/**
	 * This constructor creates a RetrievalNonRetrievalByRecipient object based
	 * on a previous DeliveryNonDeliveryToRecipient evidence.
	 * 
	 * @param details
	 *            Configuration object to set some properties
	 * @param evidence
	 *            The previous DeliveryNonDeliveryToRecipient evidence message.
	 */
	public RetrievalNonRetrievalByRecipient(EDeliveryDetails details,
			Evidence evidence)
	{
		super(details);
		init(details, evidence, true);

	}
	

	/**
	 * This constructor creates a RetrievalNonRetrievalByRecipient object based
	 * on a previous DeliveryNonDeliveryToRecipient evidence.
	 * 
	 * @param details
	 *            Configuration object to set some properties
	 * @param evidence
	 *            The previous DeliveryNonDeliveryToRecipient evidence message.
	 * @param isAcceptance
	 *            true if the evidence is a positive case otherwise false
	 */
	public RetrievalNonRetrievalByRecipient(EDeliveryDetails details,
			Evidence evidence, boolean isAcceptance)
	{
		super(details);
		init(details, evidence, isAcceptance);

	}
	
	/**
	 * This constructor creates a RetrievalNonRetrievalByRecipient object based
	 * on a previous DeliveryNonDeliveryToRecipient evidence.
	 * 
	 * @param details
	 *            Configuration object to set some properties
	 * @param evidence
	 *            The previous DeliveryNonDeliveryToRecipient evidence message.
	 */
//	public RetrievalNonRetrievalByRecipient(EDeliveryDetails details,
//			Evidence evidence, REMErrorEvent eventReson)
//	{
//		super(details);
//		init(details, evidence, false);
//		super.setEventReason(eventReson);
//	}
	// klara
	public RetrievalNonRetrievalByRecipient(EDeliveryDetails details,
			Evidence evidence, EventReasonType eventReson)
	{
		super(details);
		init(details, evidence, false);
		super.setEventReason(eventReson);
	}
	
	public RetrievalNonRetrievalByRecipient(EDeliveryDetails details,
			REMEvidenceType evidenceType, boolean isAcceptance)
	{
		initEvidenceIssuerDetailsWithEdeliveryDetails(details);
		init(details, evidenceType, isAcceptance);
	}

	/**
	 * This constructor can be used to parse a serialized
	 * RetrievalNonRetrievalByRecipient xml stream to create a JAXB evidence
	 * object.
	 * 
	 * @param details
	 *            Configuration object to set some properties
	 * @param evidenceSream
	 *            The xml input stream with the evidence xml data.
	 * @param typeOfInputStream
	 *            The type of the given InputStream. Possible values
	 *            DeliveryNonDeliveryToRecipient or
	 *            RelayREMMDAcceptanceRejection.
	 */
	public RetrievalNonRetrievalByRecipient(EDeliveryDetails details,
			InputStream evidenceSream, Evidences typeOfInputStream)
		throws SpocsWrongInputDataException
	{
		super(details);
		if (typeOfInputStream
				.equals(Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT))
			init(details, new DeliveryNonDeliveryToRecipient(details,
				evidenceSream), true);
		if (typeOfInputStream
				.equals(Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION))
			init(details, new RelayREMMDAcceptanceRejection(details,
				evidenceSream), true);
	}

	private void init(EDeliveryDetails details, Evidence previousEvidence,
			boolean isAcceptance)
	{

		evidenceType = Evidences.RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT;
		if (isAcceptance) {
			LOG.debug("Create RetrievalNonRetrievalByRecipient in success case.");
			setEventCode(Evidences.RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT
					.getSuccessEventCode());
		} else {
			LOG.debug("Create RetrievalNonRetrievalByRecipient in fault case.");
			setEventCode(Evidences.RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT
					.getFaultEventCode());
		}
		initWithPrevious(previousEvidence.getXSDObject());
		isSuccessful = isAcceptance;
	}
	
	private void init(EDeliveryDetails details, REMEvidenceType previousEvidence,
			boolean isAcceptance)
	{
		
		evidenceType = Evidences.RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT;
		if (isAcceptance) {
			LOG.debug("Create RetrievalNonRetrievalByRecipient in success case.");
			setEventCode(Evidences.RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT
					.getSuccessEventCode());
		} else {
			LOG.debug("Create RetrievalNonRetrievalByRecipient in fault case.");
			setEventCode(Evidences.RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT
					.getFaultEventCode());
		}
		initWithPrevious(previousEvidence);
		isSuccessful = isAcceptance;
	}

	/**
	 * This method serializes the underlying JAXB object.
	 * 
	 * @param out
	 *            The output stream that the information will be streamed into.
	 */
	public void serialize(OutputStream out) throws JAXBException
	{
		JaxbContextHolder
				.getSpocsJaxBContext()
				.createMarshaller()
				.marshal(
					new ObjectFactory()
							.createRetrievalNonRetrievalByRecipient(jaxbObj),
					out);

	}

	public boolean isSuccessful()
	{
		return isSuccessful;
	}

}
