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

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;

import org.etsi.uri._02640.v2.ObjectFactory;
import org.etsi.uri._02640.v2.REMEvidenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.edeliverygw.JaxbContextHolder;
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;

/**
 * This class represents a RelayREMMDFailure evidence. It helps to create the
 * underlying REMEvidenceType JAXB object of the XSD structure.
 * 
 * @author Lindemann
 * 
 */
public class RelayREMMDFailure extends Evidence
{
	private static Logger LOG = LoggerFactory
			.getLogger(RelayREMMDAcceptanceRejection.class);
	
	/**
	 * This constructor creates this RelayREMMDFailure evidence with the given
	 * JAXB object and the configuration.
	 * 
	 * @param evidenceType
	 *            The JAXB object.
	 */
	public RelayREMMDFailure(REMEvidenceType evidenceType)
	{
		super(evidenceType);
	}

	public RelayREMMDFailure(EDeliveryDetails details,
			InputStream relayREMMDFailureInpStream)
		throws SpocsWrongInputDataException
	{
		try {
			@SuppressWarnings("unchecked")
			JAXBElement<REMEvidenceType> obj = (JAXBElement<REMEvidenceType>) JaxbContextHolder
					.getSpocsJaxBContext().createUnmarshaller()
					.unmarshal(relayREMMDFailureInpStream);
			jaxbObj = obj.getValue();
		} catch (JAXBException ex) {
			throw new SpocsWrongInputDataException(
				"Error reading the RelayREMMDFailure xml stream.",
				ex);
		}
	}
	
	/**
	 * This constructor creates a RelayREMMDFailure object on base
	 * of a previous SubmissionAcceptanceRejection evidence. A success event
	 * will be set by this constructor.
	 * 
	 * @param details
	 *            Configuration object to set some properties
	 * @param submissionAcceptanceRejection
	 *            The previous SubmissionAcceptanceRejection
	 */
	public RelayREMMDFailure(EDeliveryDetails details,
			Evidence submissionAcceptanceRejection)
	{
		super(details);
		init(submissionAcceptanceRejection);
	}
	
	public RelayREMMDFailure(EDeliveryDetails details,
			REMEvidenceType submissionAcceptanceRejection)
	{
		initEvidenceIssuerDetailsWithEdeliveryDetails(details);
		init(details, submissionAcceptanceRejection);
	}
	
	public RelayREMMDFailure(REMEvidenceType singleEvidence,
			EDeliveryDetails details)
	{
		super(details);
		evidenceType = Evidences.RELAY_REM_MD_FAILURE;
		LOG.debug("Create RelayREMMDFailure.");
		setEventCode(Evidences.RELAY_REM_MD_FAILURE.getFaultEventCode());
		//REMEvidenceType singleEvidence = message.getEvidence(Evidences.SUBMISSION_ACCEPTANCE_REJECTION, message.getXSDObject().getREMMDEvidenceList());
		initWithPrevious(singleEvidence);
	}
	
	private void init(Evidence submissionAcceptanceRejection)
	{

		evidenceType = Evidences.RELAY_REM_MD_FAILURE;
		LOG.debug("Create RelayREMMDFailure in fault case.");
		setEventCode(Evidences.RELAY_REM_MD_FAILURE.getFaultEventCode());
	
		initWithPrevious(submissionAcceptanceRejection.getXSDObject());
	}
	
	private void init(EDeliveryDetails details,	REMEvidenceType submissionAcceptanceRejection)
	{
		evidenceType = Evidences.RELAY_REM_MD_FAILURE;

		LOG.debug("Create RelayREMMDFailure.");
		setEventCode(Evidences.RELAY_REM_MD_FAILURE.getFaultEventCode());
		
		initWithPrevious(submissionAcceptanceRejection);
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
				.marshal(new ObjectFactory().createRelayREMMDFailure(jaxbObj),
					out);
	}	
}
