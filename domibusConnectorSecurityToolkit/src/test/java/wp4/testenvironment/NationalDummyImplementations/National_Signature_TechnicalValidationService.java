package wp4.testenvironment.NationalDummyImplementations;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;

import eu.ecodex.dss.model.EnvironmentConfiguration;
import eu.ecodex.dss.model.token.Signature;
import eu.ecodex.dss.model.token.SignatureAttributes;
import eu.ecodex.dss.model.token.SignatureCertificate;
import eu.ecodex.dss.model.token.TechnicalTrustLevel;
import eu.ecodex.dss.model.token.TechnicalValidationResult;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.model.token.TokenValidation;
import eu.ecodex.dss.model.token.ValidationVerification;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import eu.europa.esig.dss.model.DSSDocument;

public class National_Signature_TechnicalValidationService implements ECodexTechnicalValidationService {

	private TokenValidation tknValidation;
	private TechnicalValidationResult techResult;
	private Signature sig;
	private SignatureCertificate sigCert;
	private SignatureAttributes sigAttr;
	private ValidationVerification valVeri;

	public void setEnvironmentConfiguration(EnvironmentConfiguration conf) {
	}

	@Override
	public TokenValidation create(DSSDocument businessDocument, DSSDocument detachedSignature) throws ECodexException {
			return tknValidation;
	}

	@Override
	public DSSDocument createReportPDF(Token token) throws ECodexException {
		return null;
	}
	
	// No Result
	public void setInvalid_NoResult() {
		this.tknValidation = null;
	}
	
	// Empty Result
	public void setInvalid_EmptyResult() {
		tknValidation = new TokenValidation();
	}
	
	// Invalid Result
	public void setInvalid_InvalidResult() {
		
		tknValidation = new TokenValidation();
		techResult = new TechnicalValidationResult();
		sig = new Signature();
		sigCert= new SignatureCertificate();
		sigAttr = new SignatureAttributes();
		valVeri = new ValidationVerification();
		
		
		
		try{
			sigAttr.setSignatureFormat(null); // Invalid Entry
			sigAttr.setSignatureLevel("QES");
			sigAttr.setSignatureValid(true);
			sigAttr.setStructureValid(true);

			sigCert.setIssuer(null); // Invalid Entry
			sigCert.setCertificateValid(true);
			sigCert.setValidityAtSigningTime(true);

			sig.setCertificateInformation(sigCert);
			sig.setSignatureInformation(sigAttr);
			sig.setSigningTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
			
//			valVeri.addSignatureData(sig);
			valVeri.setSignatureData(sig);
			
			techResult.setTrustLevel(TechnicalTrustLevel.SUCCESSFUL);
			techResult.setComment("This document has been successfully validated");
			
			tknValidation.setTechnicalResult(techResult);
			tknValidation.setVerificationData(valVeri);
			tknValidation.setVerificationTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

		} catch(Exception e) {
			System.err.println("Exception within the configuration of the signature based technical validation service:");
			e.printStackTrace();
		}		
	}
	
	public void setValid_AllDataPresent() {
		
		tknValidation = new TokenValidation();
		techResult = new TechnicalValidationResult();
		sig = new Signature();
		sigCert= new SignatureCertificate();
		sigAttr = new SignatureAttributes();
		valVeri = new ValidationVerification();
		
		try{
			sigAttr.setSignatureFormat("PAdES-BES");
			sigAttr.setSignatureLevel("QES");
			sigAttr.setSignatureValid(true);
			sigAttr.setStructureValid(true);

			sigCert.setIssuer("Test-Issuer");
			sigCert.setCertificateValid(true);
			sigCert.setValidityAtSigningTime(true);

			sig.setCertificateInformation(sigCert);
			sig.setSignatureInformation(sigAttr);
			sig.setSigningTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
			
//			valVeri.addSignatureData(sig);
			valVeri.setSignatureData(sig);
			
			techResult.setTrustLevel(TechnicalTrustLevel.SUCCESSFUL);
			techResult.setComment("This document has been successfully validated");
			
			tknValidation.setTechnicalResult(techResult);
			tknValidation.setVerificationData(valVeri);
			tknValidation.setVerificationTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

		} catch(Exception e) {
			System.err.println("Exception within the configuration of the signature based technical validation service:");
			e.printStackTrace();
		}
	}	
}
